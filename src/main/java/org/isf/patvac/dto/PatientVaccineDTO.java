package org.isf.patvac.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;
import org.isf.vaccine.dto.VaccineDTO;

import io.swagger.annotations.ApiModelProperty;

public class PatientVaccineDTO
{
	private int code;

	@NotNull
	@ApiModelProperty(notes = "a progr. in year", example="1", position = 1)
	private int progr;

	@NotNull
	@ApiModelProperty(notes = "the vaccine date", position = 2)
	private Date vaccineDate;

	@NotNull
	@ApiModelProperty(notes = "the patient to be vaccine", position = 3)
	private PatientDTO patient;

	@NotNull
	@ApiModelProperty(notes = "the vaccine", position = 4)
	private VaccineDTO vaccine;
	
	private int lock;
	
	private int hashCode = 0;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getProgr() {
		return progr;
	}

	public void setProgr(int progr) {
		this.progr = progr;
	}

	public Date getVaccineDate() {
		return vaccineDate;
	}

	public void setVaccineDate(Date vaccineDate) {
		this.vaccineDate = vaccineDate;
	}

	public PatientDTO getPatient() {
		return patient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public VaccineDTO getVaccine() {
		return vaccine;
	}

	public void setVaccine(VaccineDTO vaccine) {
		this.vaccine = vaccine;
	}
	
	@ApiModelProperty(hidden= true)
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

}
