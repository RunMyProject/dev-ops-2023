package myspring.checkmime.controller;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.model.Checkmime;
import myspring.checkmime.model.LayoutResponse;
import myspring.checkmime.model.ValidationFormat;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/wsrest")
public class ValidationController {

	private final static String MIME_FORMAT = "p7m";
	private final static char MIME_SEPARATOR = '.';

	@Autowired
	CheckmimeRepository checkmimeRepository;
	@Autowired
	IFileSystemStorageService fileSystemStorage;
	@Autowired
	IValidationService validationService;
	@GetMapping("/validation")
	public ResponseEntity<List<LayoutResponse>> validationAll() {

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
		String description_p7m = null;

		for(Checkmime checkmime : checkmimeList) {
			if(checkmime.getFormat().equals(MIME_FORMAT)) {
				enabled_p7m = checkmime.isEnabled();
				description_p7m = checkmime.getDescription();
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

					if(enabled_p7m && extension.equals(MIME_FORMAT)) {

						String fileWithout_p7m = fileName.substring(0, i);
						int j = fileWithout_p7m.lastIndexOf(MIME_SEPARATOR);

						if(j>0) {
							extension = fileWithout_p7m.substring(j+1); // internal extension
							is_p7m = true;
						}
						else extension = "";
					}

					Checkmime checkmimeCurrent = null;
					boolean validated = false;

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

						try {
							String descriptionMimeFormat = getMimeFormat(filePath.toFile());
							validated = descriptionMimeFormat.equals(is_p7m ? description_p7m : checkmimeCurrent.getDescription());

							System.out.println(descriptionMimeFormat);

						} catch (IOException e) {
							validated = false;
							throw new RuntimeException(e);
						}
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

	private String getMimeFormat(File f) throws IOException {

		String mimeType = null;
		try {
			InputStream is = new FileInputStream(f);
			TikaConfig tc = new TikaConfig();
			Metadata md = new Metadata();
			md.set(Metadata.RESOURCE_NAME_KEY, f.getName());
			mimeType = tc.getDetector().detect(TikaInputStream.get(is), md).toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return mimeType;
	}

}