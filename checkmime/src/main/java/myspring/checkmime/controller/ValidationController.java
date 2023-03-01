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
		List<String> files = new ArrayList<>();

		AtomicInteger itemAtomicInteger = new AtomicInteger();
		AtomicInteger pageAtomicInteger = new AtomicInteger();
		int totFiles = fileSystemStorage.listFiles().size();
		int totPages = fileSystemStorage.listFiles().size() / validationService.getPageNumber();
		int totLast = totFiles - (totPages*validationService.getPageNumber());

		List<Checkmime> checkmimeList = checkmimeRepository.findByEnabled(true);

		boolean enabled_p7m = false;

		for(Checkmime checkmime : checkmimeList) {
			if(checkmime.getFormat().equals(MIME_FORMAT)) {
				enabled_p7m = checkmime.isEnabled();
				break;
			}
		}

		for(Path path : fileSystemStorage.listFiles()) {

			String fileName = path.getFileName().toString();
			int nFile = itemAtomicInteger.incrementAndGet();

			if(nFile==1) files.clear();
			files.add(fileName);

			if(nFile==validationService.getPageNumber() || (nFile==totLast && pageAtomicInteger.get()==totPages)) {

				List<ValidationFormat> validationList = new ArrayList<>();

				for(String file : files) {

					boolean validated = false;

					String extension = "";
					int i = file.lastIndexOf(MIME_SEPARATOR);

					if(i>0) extension = file.substring(i+1);

					if(enabled_p7m && extension.equals(MIME_FORMAT)) {

						String fileWithout_p7m = file.substring(0, i);
						int j = fileWithout_p7m.lastIndexOf(MIME_SEPARATOR);

						if(j>0) extension = fileWithout_p7m.substring(j+1);
						else extension = "";
					}

					for(Checkmime checkmime : checkmimeList) {
						if(!validated) validated = extension.equalsIgnoreCase(checkmime.getFormat());
						else break;
					}

					ValidationFormat validationFormat = ValidationFormat.builder()
							.filename(file)
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

}