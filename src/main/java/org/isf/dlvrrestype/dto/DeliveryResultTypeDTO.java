package org.isf.dlvrrestype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class DeliveryResultTypeDTO {

	@NotNull
	@ApiModelProperty(notes = "code of the delivery result type", example = "M", position = 1)
	private String code;

	@NotNull
	@ApiModelProperty(notes = "description of the delivery result type", example = "MORTALITÉ MATERNELLE", position = 2)
	private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
