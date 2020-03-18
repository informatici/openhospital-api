package org.isf.dlvrtype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author gildas
 *
 */
public class DeliveryTypeDTO {

	@NotNull
	@ApiModelProperty(notes = "code of the delivery type", example = "N", position = 1)
	private String code;

	@NotNull
	@ApiModelProperty(notes = "description of the delivery type", example = "ACCOUCHEMENT NORMAL", position = 2)
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
