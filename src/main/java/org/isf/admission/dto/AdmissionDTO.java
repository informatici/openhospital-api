/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.admission.dto;

import java.time.LocalDateTime;

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
 * @author gildas
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
	private LocalDateTime admDate;

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
	private LocalDateTime opDate;

	@ApiModelProperty(notes = "operation result value is 'P' or 'N' ", example = "N", position = 16)
	private String opResult;

	@ApiModelProperty(notes = "discharge date", position = 17)
	private LocalDateTime disDate;

	@ApiModelProperty(notes = "disChargeType", position = 18)
	private DischargeTypeDTO disType;

	@ApiModelProperty(notes = "free note", position = 19)
	private String note;

	@ApiModelProperty(notes = "transfusional unit", position = 20)
	private Float transUnit;

	@ApiModelProperty(notes = "visit date", position = 21)
	private LocalDateTime visitDate;

	@ApiModelProperty(notes = "treatmentType ", position = 22)
	private PregnantTreatmentTypeDTO pregTreatmentType;

	@ApiModelProperty(notes = "delivery date", position = 23)
	private LocalDateTime deliveryDate;

	@ApiModelProperty(notes = "delivery type", position = 24)
	private DeliveryTypeDTO deliveryType;

	@ApiModelProperty(notes = "delivery type ", position = 25)
	private DeliveryResultTypeDTO deliveryResult;

	@ApiModelProperty(notes = "weight", position = 26)
	private Float weight;

	private LocalDateTime ctrlDate1;

	private LocalDateTime ctrlDate2;

	private LocalDateTime abortDate;

	@ApiModelProperty(notes = "weight", position = 30)
	private String userID;
	
	private int hashCode;
	
	@ApiModelProperty(notes = "lock", example = "0", position = 31)
	private int lock;

	@NotNull
	@ApiModelProperty(notes = "flag record deleted, values are 'Y' OR 'N' ", example = "N", position = 32)
	private String deleted;

	public int getId() {
		return this.id;
	}

	public int getAdmitted() {
		return this.admitted;
	}

	public String getType() {
		return this.type;
	}

	public WardDTO getWard() {
		return this.ward;
	}

	public int getYProg() {
		return this.yProg;
	}

	public PatientDTO getPatient() {
		return this.patient;
	}

	public LocalDateTime getAdmDate() {
		return this.admDate;
	}

	public AdmissionTypeDTO getAdmType() {
		return this.admType;
	}

	public String getFHU() {
		return this.FHU;
	}

	public DiseaseDTO getDiseaseIn() {
		return this.diseaseIn;
	}

	public DiseaseDTO getDiseaseOut1() {
		return this.diseaseOut1;
	}

	public DiseaseDTO getDiseaseOut2() {
		return this.diseaseOut2;
	}

	public DiseaseDTO getDiseaseOut3() {
		return this.diseaseOut3;
	}

	public OperationDTO getOperation() {
		return this.operation;
	}

	public LocalDateTime getOpDate() {
		return this.opDate;
	}

	public String getOpResult() {
		return this.opResult;
	}

	public LocalDateTime getDisDate() {
		return this.disDate;
	}

	public DischargeTypeDTO getDisType() {
		return this.disType;
	}

	public String getNote() {
		return this.note;
	}

	public Float getTransUnit() {
		return this.transUnit;
	}

	public LocalDateTime getVisitDate() {
		return this.visitDate;
	}

	public PregnantTreatmentTypeDTO getPregTreatmentType() {
		return this.pregTreatmentType;
	}

	public LocalDateTime getDeliveryDate() {
		return this.deliveryDate;
	}

	public DeliveryTypeDTO getDeliveryType() {
		return this.deliveryType;
	}

	public DeliveryResultTypeDTO getDeliveryResult() {
		return this.deliveryResult;
	}

	public Float getWeight() {
		return this.weight;
	}

	public LocalDateTime getCtrlDate1() {
		return this.ctrlDate1;
	}

	public LocalDateTime getCtrlDate2() {
		return this.ctrlDate2;
	}

	public LocalDateTime getAbortDate() {
		return this.abortDate;
	}

	public String getUserID() {
		return this.userID;
	}

	public String getDeleted() {
		return this.deleted;
	}

	@ApiModelProperty(hidden=true)
	public int getHashCode() {
		return hashCode;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAdmitted(int admitted) {
		this.admitted = admitted;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

	public void setYProg(int yProg) {
		this.yProg = yProg;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public void setAdmDate(LocalDateTime admDate) {
		this.admDate = admDate;
	}

	public void setAdmType(AdmissionTypeDTO admType) {
		this.admType = admType;
	}

	public void setFHU(String FHU) {
		this.FHU = FHU;
	}

	public void setDiseaseIn(DiseaseDTO diseaseIn) {
		this.diseaseIn = diseaseIn;
	}

	public void setDiseaseOut1(DiseaseDTO diseaseOut1) {
		this.diseaseOut1 = diseaseOut1;
	}

	public void setDiseaseOut2(DiseaseDTO diseaseOut2) {
		this.diseaseOut2 = diseaseOut2;
	}

	public void setDiseaseOut3(DiseaseDTO diseaseOut3) {
		this.diseaseOut3 = diseaseOut3;
	}
	public void setOperation(OperationDTO operation) {
		this.operation = operation;
	}

	public void setOpDate(LocalDateTime opDate) {
		this.opDate = opDate;
	}

	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}

	public void setDisDate(LocalDateTime disDate) {
		this.disDate = disDate;
	}

	public void setDisType(DischargeTypeDTO disType) {
		this.disType = disType;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setTransUnit(Float transUnit) {
		this.transUnit = transUnit;
	}

	public void setVisitDate(LocalDateTime visitDate) {
		this.visitDate = visitDate;
	}

	public void setPregTreatmentType(PregnantTreatmentTypeDTO pregTreatmentType) {
		this.pregTreatmentType = pregTreatmentType;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public void setDeliveryType(DeliveryTypeDTO deliveryType) {
		this.deliveryType = deliveryType;
	}

	public void setDeliveryResult(DeliveryResultTypeDTO deliveryResult) {
		this.deliveryResult = deliveryResult;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public void setCtrlDate1(LocalDateTime ctrlDate1) {
		this.ctrlDate1 = ctrlDate1;
	}

	public void setCtrlDate2(LocalDateTime ctrlDate2) {
		this.ctrlDate2 = ctrlDate2;
	}

	public void setAbortDate(LocalDateTime abortDate) {
		this.abortDate = abortDate;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
}
