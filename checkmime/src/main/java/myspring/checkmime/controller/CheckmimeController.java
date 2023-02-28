package myspring.checkmime.controller;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import myspring.checkmime.model.Checkmime;
import myspring.checkmime.repository.CheckmimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/wsrest/db")
public class CheckmimeController {

	@Autowired
	CheckmimeRepository checkmimeRepository;

	@GetMapping("/checkmimes")
	public ResponseEntity<List<Checkmime>> getAllCheckmimes(@RequestParam(required = false) String format) {
		try {
			List<Checkmime> checkmimeList = new ArrayList<>();

			if (format == null)
				checkmimeRepository.findAll().forEach(checkmimeList::add);
			else
				checkmimeRepository
						.findByFormatContaining(format)
						.forEach(checkmimeList::add);

			if (checkmimeList.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(checkmimeList, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/checkmimes/{id}")
	public ResponseEntity<Checkmime> getCheckmimeById(@PathVariable("id") long id) {
		Optional<Checkmime> checkmimeOptional = checkmimeRepository.findById(id);

		if (checkmimeOptional.isPresent()) {
			return new ResponseEntity<>(checkmimeOptional.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/checkmimes")
	public ResponseEntity<Checkmime> createCheckmime(@RequestBody Checkmime checkmime) {
		try {
			Checkmime checkmimeSaved = checkmimeRepository
					.save(Checkmime.builder()
							.format(checkmime.getFormat())
							.description(checkmime.getDescription())
							.enabled(true)
							.build());
			return new ResponseEntity<>(checkmimeSaved, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/checkmimes/{id}")
	public ResponseEntity<Checkmime> updateCheckmime(@PathVariable("id") long id,
													 @RequestBody Checkmime checkmime) {
		Optional<Checkmime> checkmimeOptional = checkmimeRepository.findById(id);

		if (checkmimeOptional.isPresent()) {
			Checkmime checkmimeToSave = checkmimeOptional.get();
			checkmimeToSave.setFormat(checkmime.getFormat());
			checkmimeToSave.setDescription(checkmime.getDescription());
			checkmimeToSave.setEnabled(checkmime.isEnabled());
			return new ResponseEntity<>(checkmimeRepository.save(checkmimeToSave), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/checkmimes/{id}")
	public ResponseEntity<HttpStatus> deleteCheckmime(@PathVariable("id") long id) {
		try {
			checkmimeRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/checkmimes")
	public ResponseEntity<HttpStatus> deleteAllCheckmimes() {
		try {
			checkmimeRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/checkmimes/enabled")
	public ResponseEntity<List<Checkmime>> findByEnabled() {
		try {
			List<Checkmime> checkmimeList = checkmimeRepository.findByEnabled(true);

			if (checkmimeList.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(checkmimeList, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
