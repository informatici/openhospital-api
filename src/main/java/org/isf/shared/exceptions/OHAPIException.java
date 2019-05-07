package org.isf.shared.exceptions;

import java.util.List;

import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.http.HttpStatus;

public class OHAPIException extends OHServiceException {
	private HttpStatus status = HttpStatus.BAD_REQUEST;
	public OHAPIException(OHExceptionMessage message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public OHAPIException(OHExceptionMessage message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
	
	
}
