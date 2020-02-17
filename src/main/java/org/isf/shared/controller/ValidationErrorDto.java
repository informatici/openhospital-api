package org.isf.shared.controller;

import java.util.Map;

public class ValidationErrorDto extends FailureDto {

	private static final long serialVersionUID = 8389822547860507326L;

	private Map<String, Object> validationErrors;

	public ValidationErrorDto(String message, Map<String, Object> validationErrors) {
		super(ResponseCode.INVALID_PARAMETER, message);
		this.validationErrors = validationErrors;
	}

	public Map<String, Object> getValidationErrors() {
		return validationErrors;
	}

	public void setValidationErrors(Map<String, Object> validationErrors) {
		this.validationErrors = validationErrors;
	}

}
