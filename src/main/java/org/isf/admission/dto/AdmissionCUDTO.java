package org.isf.admission.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author gildas
 *
 */
public class AdmissionCUDTO {

	@NotNull
	@ApiModelProperty(notes = "ward code", example="N", position = 4)
	private String wardCode;

	@NotNull
	@ApiModelProperty(notes = "patient id", example="1", position = 6)
	private Integer patientId;

	@NotNull
	@ApiModelProperty(notes = "admission type code", example="R", position = 8)
	private String admissionTypeCode;

	@ApiModelProperty(notes = "disease in code", example = "1", position = 10)
	private Integer diseaseInCode;

	@ApiModelProperty(notes = "disease out code", example = "1", position = 11)
	private Integer diseaseOut1Code;

	@ApiModelProperty(notes = "disease out code", example = "1", position = 12)
	private Integer diseaseOut2Code;

	@ApiModelProperty(notes = "disease out code", example = "1", position = 13)
	private Integer diseaseOut3Code;

	@ApiModelProperty(notes = "operation code", example = "1", position = 14)
	private String operationCode;

	@ApiModelProperty(notes = "disChargeType code", position = 18)
	private String disTypeCode;

	@ApiModelProperty(notes = "treatmentType code", position = 22)
	private String pregTreatmentTypeCode;

	@ApiModelProperty(notes = "delivery type code", position = 24)
	private String deliveryTypeCode;

	@ApiModelProperty(notes = "delivery type key", position = 25)
	private String deliveryResultCode;

	@NotNull
	private AdmissionSimpleDTO admissionSimpleDTO;

	public String getWardCode() {
		return wardCode;
	}

	public void setWardCode(String wardCode) {
		this.wardCode = wardCode;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getAdmissionTypeCode() {
		return admissionTypeCode;
	}

	public void setAdmissionTypeCode(String admissionTypeCode) {
		this.admissionTypeCode = admissionTypeCode;
	}

	public Integer getDiseaseInCode() {
		return diseaseInCode;
	}

	public void setDiseaseInCode(Integer diseaseInCode) {
		this.diseaseInCode = diseaseInCode;
	}

	public Integer getDiseaseOut1Code() {
		return diseaseOut1Code;
	}

	public void setDiseaseOut1Code(Integer diseaseOut1Code) {
		this.diseaseOut1Code = diseaseOut1Code;
	}

	public Integer getDiseaseOut2Code() {
		return diseaseOut2Code;
	}

	public void setDiseaseOut2Code(Integer diseaseOut2Code) {
		this.diseaseOut2Code = diseaseOut2Code;
	}

	public Integer getDiseaseOut3Code() {
		return diseaseOut3Code;
	}

	public void setDiseaseOut3Code(Integer diseaseOut3Code) {
		this.diseaseOut3Code = diseaseOut3Code;
	}

	public String getOperationCode() {
		return operationCode;
	}

	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}

	public String getDisTypeCode() {
		return disTypeCode;
	}

	public void setDisTypeCode(String disTypeCode) {
		this.disTypeCode = disTypeCode;
	}

	public String getPregTreatmentTypeCode() {
		return pregTreatmentTypeCode;
	}

	public void setPregTreatmentTypeCode(String pregTreatmentTypeCode) {
		this.pregTreatmentTypeCode = pregTreatmentTypeCode;
	}

	public String getDeliveryTypeCode() {
		return deliveryTypeCode;
	}

	public void setDeliveryTypeCode(String deliveryTypeCode) {
		this.deliveryTypeCode = deliveryTypeCode;
	}

	public String getDeliveryResultCode() {
		return deliveryResultCode;
	}

	public void setDeliveryResultCode(String deliveryResultCode) {
		this.deliveryResultCode = deliveryResultCode;
	}

	public AdmissionSimpleDTO getAdmissionSimpleDTO() {
		return admissionSimpleDTO;
	}

	public void setAdmissionSimpleDTO(AdmissionSimpleDTO admissionSimpleDTO) {
		this.admissionSimpleDTO = admissionSimpleDTO;
	}

}
