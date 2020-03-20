package org.isf.disease.dto;

import javax.validation.constraints.NotNull;

import org.isf.distype.dto.DiseaseTypeDTO;

public class DiseaseDTO {
	@NotNull
	private Integer code;

	@NotNull
    private String description;

	@NotNull
	private DiseaseTypeDTO diseaseType; 

	@NotNull
	private boolean opdInclude;

	@NotNull
	private boolean ipdInInclude;

	@NotNull
	private boolean ipdOutInclude;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DiseaseTypeDTO getDiseaseType() {
		return diseaseType;
	}

	public void setDiseaseType(DiseaseTypeDTO diseaseType) {
		this.diseaseType = diseaseType;
	}

	public boolean isOpdInclude() {
		return opdInclude;
	}

	public void setOpdInclude(boolean opdInclude) {
		this.opdInclude = opdInclude;
	}

	public boolean isIpdInInclude() {
		return ipdInInclude;
	}

	public void setIpdInInclude(boolean ipdInInclude) {
		this.ipdInInclude = ipdInInclude;
	}

	public boolean isIpdOutInclude() {
		return ipdOutInclude;
	}

	public void setIpdOutInclude(boolean ipdOutInclude) {
		this.ipdOutInclude = ipdOutInclude;
	}
	
	
}
