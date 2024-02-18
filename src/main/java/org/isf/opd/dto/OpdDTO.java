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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.opd.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.disease.dto.DiseaseDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

/**
 * @author gildas
 */
public class OpdDTO {

	@Schema(description = "The code of the opd", example = "3")
	private int code;

	@Schema(description = "The date of the admission", type = "string")
	private LocalDateTime date;

	@Schema(description = "The next visit date", type = "string")
	private LocalDateTime nextVisitDate;

	@Schema(description = "The admitted patient code")
	private Integer patientCode;

	@NotNull
	@Schema(description = "The patient age", example = "18")
	private int age;

	@NotNull
	@Schema(description = "The patient sex", example = "M")
	private char sex;

	// @NotNull
	@Schema(description = "The patient sex", example = "M")
	private String patientName;

	@Schema(description = "Age type", example = "null")
	private String ageType; // ADDED: Arnaud

	@NotNull
	@Schema(description = "The admission note", example = "this is out patient", maxLength = 65535)
	private String note; // ADDED: Alex

	// @NotNull
	@Schema(description = "A progr. in year for each ward", example = "18")
	private int prog_year;

	@Schema(description = "Disease")
	private DiseaseDTO disease;

	@Schema(description = "Disease 2")
	private DiseaseDTO disease2;

	@Schema(description = "Disease 3")
	private DiseaseDTO disease3;

	@NotNull
	@Schema(description = "New(N) or Reattendance(R) patient", example = "N")
	private char newPatient; // n=NEW R=REATTENDANCE

	@Schema(description = "Referral from another unit", example = "R")
	private String referralFrom; // R=referral from another unit; null=no referral from

	@Schema(description = "Referral to another unit", example = "R")
	private String referralTo; // R=referral to another unit; null=no referral to

	@Schema(description = "User id")
	private String userID;

	@Schema(description = "Lock", example = "0")
	private int lock;

	private int hashCode;

	@Schema(description = "Reasons for entry")
	private String reason; // ADDED: Arnaud

	@Schema(description = "History of a medical or psychiatric patient")
	private String anamnesis; // ADDED: Arnaud

	@Schema(description = "Allergies of patient")
	private String allergies; // ADDED: Arnaud

	@Schema(description = "Current therapies")
	private String therapies; // ADDED: Arnaud

	@Schema(description = "Prescription", maxLength = 255)
	private String prescription; // ADDED: Arnaud

	@NotNull
	@Schema(description = "Ward")
	private WardDTO ward;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAnamnesis() {
		return anamnesis;
	}

	public void setAnamnesis(String anamnesis) {
		this.anamnesis = anamnesis;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

	public String getTherapies() {
		return therapies;
	}

	public void setTherapies(String therapies) {
		this.therapies = therapies;
	}

	public String getPrescription() {
		return prescription;
	}

	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getLock() {
		return lock;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getHashCode() {
		return hashCode;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public int getCode() {
		return this.code;
	}

	public LocalDateTime getDate() {
		return this.date;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public LocalDateTime getNextVisitDate() {
		return this.nextVisitDate;
	}

	public Integer getPatientCode() {
		return this.patientCode;
	}

	public int getAge() {
		return this.age;
	}

	public char getSex() {
		return this.sex;
	}

	public String getNote() {
		return this.note;
	}

	public int getProg_year() {
		return this.prog_year;
	}

	public DiseaseDTO getDisease() {
		return this.disease;
	}

	public DiseaseDTO getDisease2() {
		return this.disease2;
	}

	public DiseaseDTO getDisease3() {
		return this.disease3;
	}

	public char getNewPatient() {
		return this.newPatient;
	}

	public String getReferralFrom() {
		return this.referralFrom;
	}

	public String getReferralTo() {
		return this.referralTo;
	}

	public String getUserID() {
		return this.userID;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setNextVisitDate(LocalDateTime nextVisitDate) {
		this.nextVisitDate = nextVisitDate;
	}

	public void setPatientCode(Integer patientCode) {
		this.patientCode = patientCode;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setProg_year(int prog_year) {
		this.prog_year = prog_year;
	}

	public void setDisease(DiseaseDTO disease) {
		this.disease = disease;
	}

	public void setDisease2(DiseaseDTO disease2) {
		this.disease2 = disease2;
	}

	public void setDisease3(DiseaseDTO disease3) {
		this.disease3 = disease3;
	}

	public void setNewPatient(char newPatient) {
		this.newPatient = newPatient;
	}

	public void setReferralFrom(String referralFrom) {
		this.referralFrom = referralFrom;
	}

	public void setReferralTo(String referralTo) {
		this.referralTo = referralTo;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public String getAgeType() {
		return ageType;
	}

	public void setAgeType(String ageType) {
		this.ageType = ageType;
	}

	public WardDTO getWard() {
		return ward;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

}
