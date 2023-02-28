package myspring.checkmime.controller;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

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

		for(Path path : fileSystemStorage.listFiles()) {

			String fileName = path.getFileName().toString();
			int nFile = itemAtomicInteger.incrementAndGet();

			if(nFile==1) files.clear();
			files.add(fileName);

			if(nFile==validationService.getPageNumber() || (nFile==totLast && pageAtomicInteger.get()==totPages)) {

				List<ValidationFormat> validationList = new ArrayList<>();

				for(String file : files) {

					boolean validated = false;

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