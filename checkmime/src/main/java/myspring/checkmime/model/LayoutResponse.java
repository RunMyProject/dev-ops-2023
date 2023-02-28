package myspring.checkmime.model;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class LayoutResponse {
		private List<ValidationFormat> validationList;
		private Integer page;
}