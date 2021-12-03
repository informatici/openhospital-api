package org.isf.therapy.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.isf.patient.dto.PatientDTO;

import io.swagger.annotations.ApiModelProperty;

public class TherapyRowDTO {
	@ApiModelProperty(notes="The therapy's ID", example = "1", position = 1)
	private Integer therapyID;

	@NotNull(message="the patient is required")
	@ApiModelProperty(notes="The patient", position = 2)
	PatientDTO patID;

	@NotNull(message="the start date is require")
	@ApiModelProperty(notes="The start date of therapy", example = "2020-07-16", position = 3)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date startDate;

	@NotNull(message="the end date is required")
	@ApiModelProperty(notes="The end date of the therapy", example = "2020-07-30", position = 4)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date endDate;

	@NotNull(message="the medical's ID is required")
	@ApiModelProperty(notes="The ID of the medical concerned by the therapy", example = "1", position = 5)
	private Integer medicalId;

	@NotNull(message="the quantity is required")
	@ApiModelProperty(notes="The quantity of medicals", example = "48", position = 6)
	private Double qty;

	@NotNull(message="the unit's ID is required")
	@ApiModelProperty(notes="The unit's ID", example = "1", position = 7)
	private Integer unitID;

	@NotNull(message="the frequence in day is required")
	@ApiModelProperty(notes="The frequence in day", example = "2", position = 8)	
	private Integer freqInDay;

	@NotNull(message="the frequence in period is required")
	@ApiModelProperty(notes="The frequence in period", example = "1", position = 9)	
	private Integer freqInPeriod;
	
	@ApiModelProperty(notes="A note for the therapy", example = "Sample note", position = 10)		
	private String note;

	@NotNull(message="the notify flag is required")
	@ApiModelProperty(notes="the notify flag: 1 if the notification need to be activated, 0 otherwise", example = "0", position = 11)	
	private Integer notifyInt;

	@NotNull(message="the sms flag is required")
	@ApiModelProperty(notes="the sms flag: 1 if sms need to be sent to patient, 0 otherwise", example = "0", position = 12)	
	private Integer smsInt;

	public TherapyRowDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TherapyRowDTO(Integer therapyID, PatientDTO patID, Date startDate, Date endDate, Integer medicalId,
			Double qty, Integer unitID, Integer freqInDay, Integer freqInPeriod, String note, Integer notifyInt,
			Integer smsInt) {
		super();
		this.therapyID = therapyID;
		this.patID = patID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.medicalId = medicalId;
		this.qty = qty;
		this.unitID = unitID;
		this.freqInDay = freqInDay;
		this.freqInPeriod = freqInPeriod;
		this.note = note;
		this.notifyInt = notifyInt;
		this.smsInt = smsInt;
	}

	public Integer getTherapyID() {
		return therapyID;
	}

	public void setTherapyID(Integer therapyID) {
		this.therapyID = therapyID;
	}

	public PatientDTO getPatID() {
		return patID;
	}

	public void setPatID(PatientDTO patID) {
		this.patID = patID;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getMedicalId() {
		return medicalId;
	}

	public void setMedicalId(Integer medicalId) {
		this.medicalId = medicalId;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public Integer getUnitID() {
		return unitID;
	}

	public void setUnitID(Integer unitID) {
		this.unitID = unitID;
	}

	public Integer getFreqInDay() {
		return freqInDay;
	}

	public void setFreqInDay(Integer freqInDay) {
		this.freqInDay = freqInDay;
	}

	public Integer getFreqInPeriod() {
		return freqInPeriod;
	}

	public void setFreqInPeriod(Integer freqInPeriod) {
		this.freqInPeriod = freqInPeriod;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getNotifyInt() {
		return notifyInt;
	}

	public void setNotifyInt(Integer notifyInt) {
		this.notifyInt = notifyInt;
	}

	public Integer getSmsInt() {
		return smsInt;
	}

	public void setSmsInt(Integer smsInt) {
		this.smsInt = smsInt;
	}
	
}
