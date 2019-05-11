package org.isf.shared.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.isf.shared.controller.FailureDto;
import org.isf.shared.controller.ResponseCode;
import org.isf.shared.controller.ValidationErrorDto;
import org.isf.utils.exception.OHServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class OHResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { OHServiceException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected FailureDto handleOHServiceException(OHServiceException ex) {
		return new FailureDto(ResponseCode.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_SERVER_ERROR.getValue());
	}

	@ExceptionHandler(value = { OHAPINotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected FailureDto handleOHAPIException(OHAPINotFoundException ex) {
		return new FailureDto(ResponseCode.NOT_FOUND, ex.getMessage());
	}

	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		final Map<String, Object> errors = new HashMap<>();
		for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.put(error.getObjectName(), error.getDefaultMessage());
		}
		final ValidationErrorDto validaErrorDto = new ValidationErrorDto(ResponseCode.INVALID_PARAMETER.getValue(), errors);
		return handleExceptionInternal(ex, validaErrorDto, headers, HttpStatus.BAD_REQUEST,
				request);
	}

}
