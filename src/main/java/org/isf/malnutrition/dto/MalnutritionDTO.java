package org.isf.malnutrition.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.isf.admission.dto.AdmissionDTO;

import io.swagger.annotations.ApiModelProperty;

public class MalnutritionDTO {
	
	@ApiModelProperty(notes="The code malnutrition control", example = "1", position = 1)
	private int code;

	@NotNull(message="The date of control is required")
	@ApiModelProperty(notes="The date of this malnutrition control", example = "1979-05-01", position = 2)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date dateSupp;

	@ApiModelProperty(notes="The date of the next malnutrition control", example = "1979-05-01", position = 3)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date dateConf;

	@NotNull(message="The admission is required")
	@ApiModelProperty(notes="The admission requesting the control", position = 4)
	private AdmissionDTO admission;

	@NotNull(message="The height is required")
	@ApiModelProperty(notes="The height of the patient", example="165", position = 5)
	private float height;

	@NotNull(message="The weight is required")
	@ApiModelProperty(notes="The weight of the patient", example="65", position = 6)
	private float weight;
	
	public MalnutritionDTO() { }
	
	public MalnutritionDTO(int aCode, Date aDateSupp,
			Date aDateConf, AdmissionDTO anAdmission, float aHeight,
			float aWeight) {
		code = aCode;
		dateSupp = aDateSupp;
		dateConf = aDateConf;
		admission = anAdmission;
		height = aHeight;
		weight = aWeight;
	}

	public void setCode(int aCode) {
		code = aCode;
	}

	public int getCode() {
		return code;
	}
	
	public AdmissionDTO getAdmission() {
		return admission;
	}

	public void setAdmission(AdmissionDTO admission) {
		this.admission = admission;
	}

	public void setDateSupp(Date aDateSupp) {
		dateSupp = aDateSupp;
	}

	public void setDateConf(Date aDateConf) {
		dateConf = aDateConf;
	}
	
	public void setHeight(float aHeight) {
		height = aHeight;
	}

	public void setWeight(float aWeight) {
		weight = aWeight;
	}

	public Date getDateSupp() {
		return dateSupp;
	}

	public Date getDateConf() {
		return dateConf;
	}

	public float getHeight() {
		return height;
	}

	public float getWeight() {
		return weight;
	}
}
