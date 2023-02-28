package myspring.checkmime.controller;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import myspring.checkmime.model.FileResponse;
import myspring.checkmime.service.IFileSystemStorageService;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wsrest/file")
public class FileController {
	@Autowired
	IFileSystemStorageService fileSytemStorage;

	@PostMapping("/uploadfile")
	public ResponseEntity<FileResponse> uploadSingleFile (@RequestParam("file") MultipartFile file) {
		String upfile = fileSytemStorage.saveFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/wsrest/download/")
				.path(upfile)
				.toUriString();

		return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(upfile,fileDownloadUri,"File uploaded with success!"));
	}

	@PostMapping("/uploadfiles")
	public ResponseEntity<List<FileResponse>> uploadMultipleFiles (@RequestParam("files") MultipartFile[] files) {

		List<FileResponse> responses = Arrays
				.asList(files)
				.stream()
				.map(
						file -> {
							String upfile = fileSytemStorage.saveFile(file);
							String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
									.path("/wsrest/download/")
									.path(upfile)
									.toUriString();
							return new FileResponse(upfile,fileDownloadUri,"File uploaded with success!");
						}
				)
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	@GetMapping("/download/{filename:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

		Resource resource = fileSytemStorage.loadFile(filename);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/getAll")
	public ResponseEntity<List<FileResponse>> getAll() {

		List<FileResponse> responses = fileSytemStorage.listFiles()
				.stream()
				.map(
						file -> {
							String fileName = file.getFileName().toString();
							String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
									.path("/wsrest/download/")
									.path(fileName)
									.toUriString();
							return new FileResponse(fileName, fileDownloadUri, "List all files with success!");
						}
				)
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}
}