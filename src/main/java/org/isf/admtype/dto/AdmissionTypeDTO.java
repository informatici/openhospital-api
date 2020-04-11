package org.isf.admtype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * Not used anymore
 * @author antonio
 *
 */

public class AdmissionTypeDTO {

	@NotNull
	@ApiModelProperty(notes = "code of the admission type", example="A", position = 1)
	private String code;

	@NotNull
	@ApiModelProperty(notes = "description of the admission type", example="AMBULANCE", position = 2)
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
