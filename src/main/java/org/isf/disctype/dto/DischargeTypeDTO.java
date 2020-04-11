package org.isf.disctype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author gildas
 *
 */
public class DischargeTypeDTO {
	@NotNull
	@ApiModelProperty(notes = "code of the discharge type", example = "SN", position = 1)
	private String code;

	@NotNull
	@ApiModelProperty(notes = "description of the discharge type", example = "SORTIE NORMALE", position = 2)
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
