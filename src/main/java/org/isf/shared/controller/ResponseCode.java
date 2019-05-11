package org.isf.shared.controller;

public enum ResponseCode {

	SUCCESS("success"), INVALID_PARAMETER("Invalid parameter error"), INTERNAL_SERVER_ERROR("Internal Server Error"), NOT_FOUND("Resource Not Found");

	private String value;

	ResponseCode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
