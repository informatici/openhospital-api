package org.isf.pregtreattype.dto;

import javax.validation.constraints.NotNull;

public class PregnantTreatmentTypeDTO {

	private String code;

	@NotNull
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
