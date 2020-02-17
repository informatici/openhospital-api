package org.isf.shared.exceptions;

import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHServiceValidationException;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class OHResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { OHServiceValidationException.class })
	protected ResponseEntity<Object> handleOHServiceValidationException(OHServiceException ex) {
		return buildResponseEntity(new OHAPIError(HttpStatus.BAD_REQUEST, ex));
	}
	@ExceptionHandler(value = { OHServiceException.class })
	protected ResponseEntity<Object> handleOHServiceException(OHServiceException ex) {
		return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}

	@ExceptionHandler(value = { OHAPIException.class })
	protected ResponseEntity<Object> handleOHAPIException(OHAPIException ex) {
		return buildResponseEntity(new OHAPIError(ex.getStatus(), ex));
	}

	private ResponseEntity<Object> buildResponseEntity(OHAPIError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
