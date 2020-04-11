package org.isf.admission.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.operation.dto.OperationDTO;
import org.isf.patient.dto.PatientDTO;
import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author gildas
 *
 */
public class AdmissionDTO {

	@ApiModelProperty(notes = "admission key", example = "12", position = 1)
	private int id;

	@NotNull
	@ApiModelProperty(notes = "if admitted or not", example = "0", position = 2)
	private int admitted;

	@NotNull
	@ApiModelProperty(notes = "type of admission", example = "N", position = 3)
	private String type;

	@ApiModelProperty(notes = "ward", position = 4)
	private WardDTO ward;

	@NotNull
	@ApiModelProperty(notes = "a progr. in year for each ward", example = "1", position = 5)
	private int yProg;

	@ApiModelProperty(notes = "patient", position = 6)
	private PatientDTO patient;

	@NotNull
	@ApiModelProperty(notes = "admission date", position = 7)
	private Date admDate;

	@ApiModelProperty(notes = "admission type", position = 8)
	private AdmissionTypeDTO admType;

	@ApiModelProperty(notes = "FromHealthUnit", position = 9)
	private String FHU;

	@ApiModelProperty(notes = "disease in ", position = 10)
	private DiseaseDTO diseaseIn;

	@ApiModelProperty(notes = "disease out ", position = 11)
	private DiseaseDTO diseaseOut1;

	@ApiModelProperty(notes = "disease out ", position = 12)
	private DiseaseDTO diseaseOut2;

	@ApiModelProperty(notes = "disease out ",  position = 13)
	private DiseaseDTO diseaseOut3;

	@ApiModelProperty(notes = "operation ", position = 14)
	private OperationDTO operation;

	@ApiModelProperty(notes = "operation date", position = 15)
	private Date opDate;

	@ApiModelProperty(notes = "operation result value is 'P' or 'N' ", example = "N", position = 16)
	private String opResult;

	@ApiModelProperty(notes = "discharge date", position = 17)
	private Date disDate;

	@ApiModelProperty(notes = "disChargeType ", position = 18)
	private DischargeTypeDTO disType;

	@ApiModelProperty(notes = "free note", position = 19)
	private String note;

	@ApiModelProperty(notes = "transfusional unit", position = 20)
	private Float transUnit;

	@ApiModelProperty(notes = "visite date", position = 21)
	private Date visitDate;

	@ApiModelProperty(notes = "treatmentType ", position = 22)
	private PregnantTreatmentTypeDTO pregTreatmentType;

	@ApiModelProperty(notes = "delivery date", position = 23)
	private Date deliveryDate;

	@ApiModelProperty(notes = "delivery type", position = 24)
	private DeliveryTypeDTO deliveryType;

	@ApiModelProperty(notes = "delivery type ", position = 25)
	private DeliveryResultTypeDTO deliveryResult;

	@ApiModelProperty(notes = "weight", position = 26)
	private Float weight;

	private Date ctrlDate1;

	private Date ctrlDate2;

	private Date abortDate;

	@ApiModelProperty(notes = "weight", position = 30)
	private String userID;

	@ApiModelProperty(notes = "lock", example = "0", position = 31)
	private int lock;

	@NotNull
	@ApiModelProperty(notes = "flag record deleted, values are 'Y' OR 'N' ", example = "N", position = 32)
	private String deleted;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdmitted() {
		return admitted;
	}

	public void setAdmitted(int admitted) {
		this.admitted = admitted;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public WardDTO getWard() {
		return ward;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

	public int getyProg() {
		return yProg;
	}

	public void setyProg(int yProg) {
		this.yProg = yProg;
	}

	public PatientDTO getPatient() {
		return patient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public Date getAdmDate() {
		return admDate;
	}

	public void setAdmDate(Date admDate) {
		this.admDate = admDate;
	}

	public AdmissionTypeDTO getAdmType() {
		return admType;
	}

	public void setAdmType(AdmissionTypeDTO admType) {
		this.admType = admType;
	}

	public String getFHU() {
		return FHU;
	}

	public void setFHU(String fHU) {
		FHU = fHU;
	}

	public DiseaseDTO getDiseaseIn() {
		return diseaseIn;
	}

	public void setDiseaseIn(DiseaseDTO diseaseIn) {
		this.diseaseIn = diseaseIn;
	}

	public DiseaseDTO getDiseaseOut1() {
		return diseaseOut1;
	}

	public void setDiseaseOut1(DiseaseDTO diseaseOut1) {
		this.diseaseOut1 = diseaseOut1;
	}

	public DiseaseDTO getDiseaseOut2() {
		return diseaseOut2;
	}

	public void setDiseaseOut2(DiseaseDTO diseaseOut2) {
		this.diseaseOut2 = diseaseOut2;
	}

	public DiseaseDTO getDiseaseOut3() {
		return diseaseOut3;
	}

	public void setDiseaseOut3(DiseaseDTO diseaseOut3) {
		this.diseaseOut3 = diseaseOut3;
	}

	public OperationDTO getOperation() {
		return operation;
	}

	public void setOperation(OperationDTO operation) {
		this.operation = operation;
	}

	public Date getOpDate() {
		return opDate;
	}

	public void setOpDate(Date opDate) {
		this.opDate = opDate;
	}

	public String getOpResult() {
		return opResult;
	}

	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}

	public Date getDisDate() {
		return disDate;
	}

	public void setDisDate(Date disDate) {
		this.disDate = disDate;
	}

	public DischargeTypeDTO getDisType() {
		return disType;
	}

	public void setDisType(DischargeTypeDTO disType) {
		this.disType = disType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Float getTransUnit() {
		return transUnit;
	}

	public void setTransUnit(Float transUnit) {
		this.transUnit = transUnit;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public PregnantTreatmentTypeDTO getPregTreatmentType() {
		return pregTreatmentType;
	}

	public void setPregTreatmentType(PregnantTreatmentTypeDTO pregTreatmentType) {
		this.pregTreatmentType = pregTreatmentType;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public DeliveryTypeDTO getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryTypeDTO deliveryType) {
		this.deliveryType = deliveryType;
	}

	public DeliveryResultTypeDTO getDeliveryResult() {
		return deliveryResult;
	}

	public void setDeliveryResult(DeliveryResultTypeDTO deliveryResult) {
		this.deliveryResult = deliveryResult;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Date getCtrlDate1() {
		return ctrlDate1;
	}

	public void setCtrlDate1(Date ctrlDate1) {
		this.ctrlDate1 = ctrlDate1;
	}

	public Date getCtrlDate2() {
		return ctrlDate2;
	}

	public void setCtrlDate2(Date ctrlDate2) {
		this.ctrlDate2 = ctrlDate2;
	}

	public Date getAbortDate() {
		return abortDate;
	}

	public void setAbortDate(Date abortDate) {
		this.abortDate = abortDate;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

}
