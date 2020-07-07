package org.isf.medicalstockward.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.medical.dto.MedicalDTO;
import org.isf.patient.dto.PatientDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.annotations.ApiModelProperty;

public class MovementWardDTO {
	
	@ApiModelProperty(notes="The movement ward's code", example="1", position = 1)
	private int code;
	
	@NotNull
	@ApiModelProperty(notes="The ward", position = 2)
	private WardDTO ward;
	
	@NotNull
	@ApiModelProperty(notes="The movement ward's date", example="2020-06-07", position = 3)
	private Date date;
	
	@NotNull
	@ApiModelProperty(notes="Indicates if the movement is associated to a patient or no ", example="false", position = 4)
	private boolean isPatient;
	
	@ApiModelProperty(notes="The patient in case the movement is associated to a patient", position = 5)
	private PatientDTO patient;
	
	@ApiModelProperty(notes="The patient's age in case the movement is associated to a patient", example="21", position = 6)
	private int age;
	
	@ApiModelProperty(notes="The patient's weight in case the movement is associated to a patient", example="75", position = 7)
	private float weight;
	
	@NotNull
	@ApiModelProperty(notes="The description of the movement", example="stock transfer from pharmacy to laboratory", position = 8)
	private String description;
	
	@ApiModelProperty(notes="The medical concerned by the movement", position = 9)
	private MedicalDTO medical;
	
	@NotNull
	@ApiModelProperty(notes="The quantity of the medical concerned by the movement", example="145", position = 10)
	private Double quantity;
	
	@NotNull
	@ApiModelProperty(notes="The mesure's unit of the medical concerned by the movement", example="pct", position = 11)
	private String units;
	
	@ApiModelProperty(notes="The ward to which the movement is done", position = 12)
	private WardDTO wardTo;
	
	@ApiModelProperty(notes="The ward from which the movement is done", position = 13)
	private WardDTO wardFrom;
	
	public MovementWardDTO() {
		// TODO Auto-generated constructor stub
	}

	public MovementWardDTO(int code, WardDTO ward, Date date, boolean isPatient, PatientDTO patient, int age,
			float weight, String description, MedicalDTO medical, Double quantity, String units, WardDTO wardTo,
			WardDTO wardFrom) {
		super();
		this.code = code;
		this.ward = ward;
		this.date = date;
		this.isPatient = isPatient;
		this.patient = patient;
		this.age = age;
		this.weight = weight;
		this.description = description;
		this.medical = medical;
		this.quantity = quantity;
		this.units = units;
		this.wardTo = wardTo;
		this.wardFrom = wardFrom;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public WardDTO getWard() {
		return ward;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isPatient() {
		return isPatient;
	}

	public void setPatient(boolean isPatient) {
		this.isPatient = isPatient;
	}

	public PatientDTO getPatient() {
		return patient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MedicalDTO getMedical() {
		return medical;
	}

	public void setMedical(MedicalDTO medical) {
		this.medical = medical;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public WardDTO getWardTo() {
		return wardTo;
	}

	public void setWardTo(WardDTO wardTo) {
		this.wardTo = wardTo;
	}

	public WardDTO getWardFrom() {
		return wardFrom;
	}

	public void setWardFrom(WardDTO wardFrom) {
		this.wardFrom = wardFrom;
	}
	
}
