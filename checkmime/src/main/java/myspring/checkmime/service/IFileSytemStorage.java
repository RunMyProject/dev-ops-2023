package myspring.checkmime.service;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface IFileSytemStorage {
	void init();
	String saveFile(MultipartFile file);
	Resource loadFile(String fileName);
	void deleteAll();
	List<Path> listFiles();
}