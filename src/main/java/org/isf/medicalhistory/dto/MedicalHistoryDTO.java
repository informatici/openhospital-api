/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.medicalhistory.dto;

import java.time.LocalDateTime;

import org.isf.patient.dto.PatientDTO;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Class representing the medical history of a patient")
public class MedicalHistoryDTO {

	private Integer id;

	@NotNull
	@Schema(description = "The patient identifier linked to this medical history")
	private PatientDTO patient;

	@Schema(description = "Lock", example = "0")
	private int lock;

	private Integer siblingRank;

	private String termPregnancy;

	private String deliveryMode;

	private String apgarScore;

	private Double birthWeight;

	private String vaccinationState;

	private String antiMalarialProphylaxis;

	private String diet;

	private String deParasitization;

	private String psychomotorDev;

	private String somaticGrowth;

	private Boolean ironSupplement;

	private Boolean folicAcidSupplement;
	private Boolean vitASupplement;

	private String otherSupplements;

	private Boolean transfusion;

	private LocalDateTime lastTransfusionDate;

	private Boolean sickleCell;

	private Boolean drugAllergy;

	private String allergyPrecision;

	private String hemylosis;

	private String otherPersonalPathologies;

	private String otherFamilyPathologies;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public PatientDTO getPatient() {
		return patient;
	}
	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}
	public Integer getSiblingRank() {
		return siblingRank;
	}
	public void setSiblingRank(Integer siblingRank) {
		this.siblingRank = siblingRank;
	}

	public String getTermPregnancy() {
		return termPregnancy;
	}
	public void setTermPregnancy(String termPregnancy) {
		this.termPregnancy = termPregnancy;
	}

	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public String getApgarScore() {
		return apgarScore;
	}
	public void setApgarScore(String apgarScore) {
		this.apgarScore = apgarScore;
	}

	public Double getBirthWeight() {
		return birthWeight;
	}
	public void setBirthWeight(Double birthWeight) {
		this.birthWeight = birthWeight;
	}
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getVaccinationState() {
		return vaccinationState;
	}
	public void setVaccinationState(String vaccinationState) {
		this.vaccinationState = vaccinationState;
	}

	public String getAntiMalarialProphylaxis() {
		return antiMalarialProphylaxis;
	}
	public void setAntiMalarialProphylaxis(String antiMalarialProphylaxis) {
		this.antiMalarialProphylaxis = antiMalarialProphylaxis;
	}

	public String getDiet() {
		return diet;
	}
	public void setDiet(String diet) {
		this.diet = diet;
	}

	public String getDeParasitization() {
		return deParasitization;
	}
	public void setDeParasitization(String deParasitization) {
		this.deParasitization = deParasitization;
	}

	public String getPsychomotorDev() {
		return psychomotorDev;
	}
	public void setPsychomotorDev(String psychomotorDev) {
		this.psychomotorDev = psychomotorDev;
	}

	public String getSomaticGrowth() {
		return somaticGrowth;
	}
	public void setSomaticGrowth(String somaticGrowth) {
		this.somaticGrowth = somaticGrowth;
	}

	public Boolean getIronSupplement() {
		return ironSupplement;
	}
	public void setIronSupplement(Boolean ironSupplement) {
		this.ironSupplement = ironSupplement;
	}

	public Boolean getFolicAcidSupplement() {
		return folicAcidSupplement;
	}
	public void setFolicAcidSupplement(Boolean folicAcidSupplement) {
		this.folicAcidSupplement = folicAcidSupplement;
	}

	public Boolean getVitASupplement() {
		return vitASupplement;
	}
	public void setVitASupplement(Boolean vitASupplement) {
		this.vitASupplement = vitASupplement;
	}

	public String getOtherSupplements() {
		return otherSupplements;
	}
	public void setOtherSupplements(String otherSupplements) {
		this.otherSupplements = otherSupplements;
	}

	public Boolean getTransfusion() {
		return transfusion;
	}
	public void setTransfusion(Boolean transfusion) {
		this.transfusion = transfusion;
	}

	public LocalDateTime getLastTransfusionDate() {
		return lastTransfusionDate;
	}
	public void setLastTransfusionDate(LocalDateTime lastTransfusionDate) {
		this.lastTransfusionDate = lastTransfusionDate;
	}

	public Boolean getSickleCell() {
		return sickleCell;
	}
	public void setSickleCell(Boolean sickleCell) {
		this.sickleCell = sickleCell;
	}

	public Boolean getDrugAllergy() {
		return drugAllergy;
	}
	public void setDrugAllergy(Boolean drugAllergy) {
		this.drugAllergy = drugAllergy;
	}

	public String getAllergyPrecision() {
		return allergyPrecision;
	}
	public void setAllergyPrecision(String allergyPrecision) {
		this.allergyPrecision = allergyPrecision;
	}

	public String getHemylosis() {
		return hemylosis;
	}
	public void setHemylosis(String hemylosis) {
		this.hemylosis = hemylosis;
	}

	public String getOtherPersonalPathologies() {
		return otherPersonalPathologies;
	}
	public void setOtherPersonalPathologies(String otherPersonalPathologies) {
		this.otherPersonalPathologies = otherPersonalPathologies;
	}

	public String getOtherFamilyPathologies() {
		return otherFamilyPathologies;
	}
	public void setOtherFamilyPathologies(String otherFamilyPathologies) {
		this.otherFamilyPathologies = otherFamilyPathologies;
	}
}
