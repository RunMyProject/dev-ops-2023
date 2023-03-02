package myspring.checkmime.controller;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.exception.ValidationException;
import myspring.checkmime.model.Checkmime;
import myspring.checkmime.model.LayoutResponse;
import myspring.checkmime.model.ValidationFormat;
import myspring.checkmime.properties.FileUploadProperties;
import myspring.checkmime.repository.CheckmimeRepository;
import myspring.checkmime.service.IFileSystemStorageService;
import myspring.checkmime.service.IValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;

import org.apache.tika.*;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.config.TikaConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/wsrest")
public class ValidationController {

	private final static String MIME_FORMAT = "p7m";
	private final static char MIME_SEPARATOR = '.';
	private final static String TMP_FILE_PREFIX = "_tmp_";
	private final static String PERL_COMMANDLINE = "perl -ne 's/^.*%PDF/%PDF/; print if /%PDF/../%%EOF/' ";
	private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	private Path homeDirectory = null;

	@Autowired
	CheckmimeRepository checkmimeRepository;
	@Autowired
	IFileSystemStorageService fileSystemStorage;
	@Autowired
	IValidationService validationService;
	@Autowired
	FileUploadProperties fileUploadProperties;

	@GetMapping("/validation")
	public ResponseEntity<List<LayoutResponse>> validationAll() {

		if(homeDirectory==null) {
			String userDirectory = System.getProperty("user.dir");
			this.homeDirectory = Paths.get(userDirectory + fileUploadProperties.getLocation())
					.toAbsolutePath()
					.normalize();
		}

		List<LayoutResponse> layoutResponses = new ArrayList<>();
		List<Path> files = new ArrayList<>();

		AtomicInteger itemAtomicInteger = new AtomicInteger();
		AtomicInteger pageAtomicInteger = new AtomicInteger();
		int totFiles = fileSystemStorage.listFiles().size();
		int totPages = fileSystemStorage.listFiles().size() / validationService.getPageNumber();
		int totLast = totFiles - (totPages*validationService.getPageNumber());

		List<Checkmime> checkmimeList = checkmimeRepository.findByEnabled(true);

		// Initialize p7m format
		//
		boolean enabled_p7m = false;

		for(Checkmime checkmime : checkmimeList) {
			if(checkmime.getFormat().equals(MIME_FORMAT)) {
				enabled_p7m = checkmime.isEnabled();
				break;
			}
		}

		for(Path path : fileSystemStorage.listFiles()) {

			int nFile = itemAtomicInteger.incrementAndGet();

			if(nFile==1) files.clear();
			files.add(path);

			if(nFile==validationService.getPageNumber() || (nFile==totLast && pageAtomicInteger.get()==totPages)) {

				List<ValidationFormat> validationList = new ArrayList<>();

				for(Path filePath : files) {

					String fileName = filePath.getFileName().toString();

					String extension = "";
					int i = fileName.lastIndexOf(MIME_SEPARATOR);

					if(i>0) extension = fileName.substring(i+1);

					boolean is_p7m = false;
					String fileWithout_p7m = null;

					if(enabled_p7m && extension.equals(MIME_FORMAT)) {

						fileWithout_p7m = fileName.substring(0, i);
						int j = fileWithout_p7m.lastIndexOf(MIME_SEPARATOR);

						if(j>0) {
							// internal extension of p7m
							//
							extension = fileWithout_p7m.substring(j+1);
							is_p7m = true;
						}
						else extension = "";
					}

					// Initialize validation
					//
					Checkmime checkmimeCurrent = null;
					boolean validated = false;

					// First validation: check extension format in DB
					//
					for(Checkmime checkmime : checkmimeList) {
						if(!validated) {
							validated = extension.equalsIgnoreCase(checkmime.getFormat());
							if(validated) {
								checkmimeCurrent = checkmime;
								break;
							}
						}
					}

					if(validated) {

						String descriptionMimeFormat = null;

						if(is_p7m) {

							// initialize file name tmp
							//
							String tmpFileName = homeDirectory.toString() + "/" + TMP_FILE_PREFIX + fileWithout_p7m;

							// preparing commandLine process builder
							//
							String commandLine = PERL_COMMANDLINE + homeDirectory.toString() + "/" + fileName +
									" &>" + tmpFileName;
							ProcessBuilder builder = new ProcessBuilder(isWindows ? "cmd.exe" : "/bin/bash", isWindows ? "/c" : "-c",
									commandLine
							);

							// crating logs files
							//
							builder.redirectOutput(new File("perl_output_log.txt"));
							builder.redirectError(new File("perl_error_log.txt"));

							// creating a new process
							//
							Process myProcess = null;
							try {
								myProcess = builder.start();
								// waits for the process until you terminate
								//
								myProcess.waitFor();
								// check mime without p7m crypting
								//
								File tmpFile = new File(tmpFileName);
								descriptionMimeFormat = getMimeFormat(tmpFile);
								if (!tmpFile.delete()) {
									throw new ValidationException("Deleting the file: " + tmpFile.getName());
								}
							}
							catch (IOException e) {
								throw new ValidationException("Process Validation: " + e.getMessage());
							}
							catch (InterruptedException e) {
								throw new ValidationException("Process Validation: " + e.getMessage());
							}
						} else {
							descriptionMimeFormat = getMimeFormat(filePath.toFile());
						}

						if(descriptionMimeFormat==null || descriptionMimeFormat.isEmpty()) {
							throw new ValidationException("Cannot read mime format from Tika!");
						}

						// Second validation: check validation mime description in DB after Tika extraction
						//
						validated = descriptionMimeFormat.equals(checkmimeCurrent.getDescription());
					}

					ValidationFormat validationFormat = ValidationFormat.builder()
							.filename(fileName)
							.validated(validated)
							.build();
					validationList.add(validationFormat);
				}

				LayoutResponse layoutResponse = LayoutResponse.builder()
						.validationList(validationList)
						.page(pageAtomicInteger.incrementAndGet())
						.build();
				layoutResponses.add(layoutResponse);

				itemAtomicInteger.set(0);
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(layoutResponses);
	}

	private String getMimeFormat(File f) {

		String mimeType = null;
		try {
			InputStream is = new FileInputStream(f);
			TikaConfig tc = new TikaConfig();
			Metadata md = new Metadata();
			md.set(Metadata.RESOURCE_NAME_KEY, f.getName());
			mimeType = tc.getDetector().detect(TikaInputStream.get(is), md).toString();
		}
		catch (org.apache.tika.exception.TikaException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return mimeType;
	}

}