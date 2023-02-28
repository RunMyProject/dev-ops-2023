package myspring.checkmime.service;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.properties.PageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service
public class ValidationService implements IValidationService {
	private final Integer pageNumber;
	@Autowired
	public ValidationService(PageProperties pageProperties) {
		this.pageNumber = Integer.parseInt(pageProperties.getNumber());
	}
	@Override
	@PostConstruct
	public void init() {
		System.out.println("SIZE PAGE OBJECTS = " + this.pageNumber);
	}
	@Override
	public Integer getPageNumber() {
		return pageNumber;
	}
}