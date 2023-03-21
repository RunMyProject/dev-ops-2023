package myspring.checkmime;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.config.JwtTokenUtil;
import myspring.checkmime.properties.FileUploadProperties;
import myspring.checkmime.service.FileSystemStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@RestController
@ConfigurationPropertiesScan
@EnableConfigurationProperties({FileUploadProperties.class})
public class CheckmimeApplication {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public static void main(String[] args) {
		SpringApplication.run(CheckmimeApplication.class, args);
	}
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@Bean
	CommandLineRunner init(FileSystemStorageService fileSystemStorageService) {
		// System.out.println("INIT UPLOAD");
		return (args) -> {
			fileSystemStorageService.deleteAll();

			// called in @PostConstruct in FileController with injection
			// @Autowired IFileSytemStorage fileSytemStorage
			//
			fileSystemStorageService.init();
		};
	}
}