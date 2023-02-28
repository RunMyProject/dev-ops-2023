package myspring.checkmime.exception;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileStorageException extends RuntimeException {
	private String message;
}