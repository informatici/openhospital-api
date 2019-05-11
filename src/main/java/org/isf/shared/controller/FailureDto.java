package org.isf.shared.controller;

public class FailureDto extends ResponseDto {

	private static final long serialVersionUID = -6193791376259942266L;

	private String message;

	public FailureDto(ResponseCode responseCode, String message) {
		super(responseCode, false);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
