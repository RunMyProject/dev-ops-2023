package myspring.checkmime.service;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import myspring.checkmime.exception.FileFormatNotFoundException;
import myspring.checkmime.exception.FileStorageException;
import myspring.checkmime.properties.FileUploadProperties;

@Service
public class FileSystemStorageService implements IFileSytemStorage {
	private final Path dirLocation;
	@Autowired
	public FileSystemStorageService(FileUploadProperties fileUploadProperties) {
		String userDirectory = System.getProperty("user.dir");
		this.dirLocation = Paths.get(userDirectory + fileUploadProperties.getLocation())
				.toAbsolutePath()
				.normalize();
	}

	@Override
	@PostConstruct
	public void init() {
		System.out.println("DIR UPLOAD = " + this.dirLocation);
		try {
			Files.createDirectories(this.dirLocation);
		}
		catch (Exception ex) {
			throw new FileStorageException("Could not create upload dir!");
		}
	}

	@Override
	public String saveFile(MultipartFile file) {
		try {
			String fileName = file.getOriginalFilename();
			Path dfile = this.dirLocation.resolve(fileName);
			Files.copy(file.getInputStream(), dfile,StandardCopyOption.REPLACE_EXISTING);
			return fileName;

		} catch (Exception e) {
			throw new FileStorageException("Could not upload file");
		}
	}

	@Override
	public Resource loadFile(String fileName) {
		try {
			Path file = this.dirLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new FileFormatNotFoundException("Could not find file");
			}
		}
		catch (MalformedURLException e) {
			throw new FileFormatNotFoundException("Could not download file");
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(this.dirLocation.toFile());
	}

	@Override
	public List<Path> listFiles() {

		List<Path> result;
		try (Stream<Path> walk = Files.walk(this.dirLocation)) {
			result = walk.filter(Files::isRegularFile)
					.collect(Collectors.toList());
		}
		catch (IOException e) {
			throw new FileStorageException("Could not load all files");
		}
		return result;
	}

}