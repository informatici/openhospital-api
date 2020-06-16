package org.isf.medtype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class MedicalTypeDTO {
	@NotNull
	@ApiModelProperty(notes="Code of the medical type", example = "M", position = 1)
	private String code;

	@NotNull
	@ApiModelProperty(notes="Description of the medical type", example = "Medical material", position = 1)
	private String description;
	
	public MedicalTypeDTO() 
    {
		super();
    }
	
	public MedicalTypeDTO(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}
	
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
