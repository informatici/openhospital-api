package org.isf.shared.controller;

import java.io.Serializable;

public class ResponseDto implements Serializable {

	private static final long serialVersionUID = -2825718865289305256L;

	private ResponseCode responseCode;
	private boolean success;

	public ResponseDto(ResponseCode responseCode, boolean success) {
		this.responseCode = responseCode;
		this.success = success;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
