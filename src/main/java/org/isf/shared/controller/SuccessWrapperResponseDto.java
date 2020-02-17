package org.isf.shared.controller;

public class SuccessWrapperResponseDto<T> extends SuccessDto {

	private static final long serialVersionUID = -6093669082308045328L;

	private T response;

	public SuccessWrapperResponseDto(T response) {
		this.response = response;
	}

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
