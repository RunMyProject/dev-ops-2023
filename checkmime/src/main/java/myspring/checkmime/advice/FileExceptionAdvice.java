package myspring.checkmime.advice;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import myspring.checkmime.exception.FileFormatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import myspring.checkmime.model.ResponseError;

@ControllerAdvice
public class FileExceptionAdvice extends ResponseEntityExceptionHandler {
	@ExceptionHandler(FileFormatNotFoundException.class)
	public ResponseEntity<Object> handleFileNotFoundException(FileFormatNotFoundException exc) {

		List<String> details = new ArrayList<String>();
		details.add(exc.getMessage());
		ResponseError err = new ResponseError(LocalDateTime.now(), "File Not Found" ,details);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException exc) {

		List<String> details = new ArrayList<String>();
		details.add(exc.getMessage());
		ResponseError err = new ResponseError(LocalDateTime.now(), "File Size Exceeded" ,details);
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(err);
	}
}