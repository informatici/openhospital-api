package org.isf.medicalstockward.dto;

import javax.validation.constraints.NotNull;

import org.isf.medical.dto.MedicalDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.annotations.ApiModelProperty;

public class MedicalWardIdDTO {
	@NotNull
	@ApiModelProperty(notes="The ward", position = 1)
	private WardDTO ward;
	
	@NotNull
	@ApiModelProperty(notes="The medical", position = 2)
	private MedicalDTO medical;
	
	public MedicalWardIdDTO() {
		super();
	}

	public MedicalWardIdDTO(WardDTO ward, MedicalDTO medical) {
		super();
		this.ward = ward;
		this.medical = medical;
	}

	public WardDTO getWard() {
		return ward;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

	public MedicalDTO getMedical() {
		return medical;
	}

	public void setMedical(MedicalDTO medical) {
		this.medical = medical;
	}
	
	
}
