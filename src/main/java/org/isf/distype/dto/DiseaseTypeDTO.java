package org.isf.distype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a disease type")
public class DiseaseTypeDTO {
	@NotNull
	@ApiModelProperty(notes = "Disease type code")
	private String code;

	@NotNull
	@ApiModelProperty(notes = "Disease type description")
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