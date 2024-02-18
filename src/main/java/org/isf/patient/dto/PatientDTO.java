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
package org.isf.patient.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.drew.lang.annotations.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(description = "Class representing a patient")
public class PatientDTO {

	@Schema(description = "Code of the Patient", example = "1")
	private Integer code;

	@NotNull
	@Schema(description = "First name of the patient", example = "Mario", maxLength = 50)
	private String firstName;

	@NotNull
	@Schema(description = "Last name of the patient", example = "Rossi", maxLength = 50)
	private String secondName;

	private String name;

	@Schema(description = "Birth date", example = "1979-05-01", type = "string")
	private LocalDate birthDate;

	@NotNull
	@Schema(description = "Age", example = "40")
	private int age;

	@Schema(description = "Age type", example = "null")
	private String agetype;

	@NotNull
	@Schema(description = "Sex", allowableValues = { "M", "F" }, example = "M")
	private char sex;

	@Schema(description = "Address", example = "Via Roma, 12", maxLength = 50)
	private String address;

	@NotNull
	@Schema(description = "City", example = "Verona", maxLength = 50)
	private String city;

	@Schema(description = "NextKin", example = "John Doe", maxLength = 50)
	private String nextKin;

	@Schema(description = "Telephone", example = "+393456789012", maxLength = 50)
	private String telephone;

	@Schema(description = "Note", example = "Test insert new patient", maxLength = 65535)
	private String note;

	@NotNull
	@Schema(description = "Mother's name", example = "Roberta", maxLength = 50)
	private String motherName;

	@Schema(description = "Mother's status (D=dead, A=alive)", allowableValues = { "D", "A" }, example = "A")
	private char mother;

	@NotNull
	@Schema(description = "Father's name", example = "Giuseppe", maxLength = 50)
	private String fatherName;

	@Schema(description = "Father's status (D=dead, A=alive)", allowableValues = { "D", "A" }, example = "D")
	private char father;

	@NotNull
	@Schema(description = "Blood type (0-/+, A-/+ , B-/+, AB-/+)", allowableValues = { "0-", "0+", "A-", "A+", "B-",
			"B+", "AB-", "AB+" }, example = "A+")
	private String bloodType;

	@Schema(description = "HasInsurance (Y=Yes, N=no)", allowableValues = { "Y", "N" }, example = "N")
	private char hasInsurance;

	@Schema(description = "Parent together (Y=Yes, N=no)", allowableValues = { "Y", "N" }, example = "N")
	private char parentTogether;

	@Schema(description = "Tax code", example = "RSSMRA79E01L781N", maxLength = 30)
	private String taxCode;

	@Schema(description = "Lock", example = "0")
	private int lock;

	@Schema(description = "BlobPhoto", example = "")
	private byte[] blobPhoto;

	private int hashCode;

	@Nullable
	@Schema(description = "Allergies of patient", maxLength = 255)
	private String allergies;

	@Nullable
	@Schema(description = "Current anamnesis", maxLength = 255)
	private String anamnesis;

	@Schema(description = "Status", example = "I")
	private PatientSTATUS status;

	@Schema(description = "Consensus flag", example = "true")
	private boolean consensusFlag;

	@Schema(description = "Consensus service flag", example = "true")
	private boolean consensusServiceFlag;

	public boolean isConsensusFlag() {
		return consensusFlag;
	}

	public void setConsensusFlag(boolean consensusFlag) {
		this.consensusFlag = consensusFlag;
	}

	public boolean isConsensusServiceFlag() {
		return consensusServiceFlag;
	}

	public void setConsensusServiceFlag(boolean consensusServiceFlag) {
		this.consensusServiceFlag = consensusServiceFlag;
	}

	public int getLock() {
		return lock;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

	public String getAnamnesis() {
		return anamnesis;
	}

	public void setAnamnesis(String anamnesis) {
		this.anamnesis = anamnesis;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public Integer getCode() {
		return code;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public String getName() {
		return name;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public String getNextKin() {
		return nextKin;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getHashCode() {
		return hashCode;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getSecondName() {
		return this.secondName;
	}

	public LocalDate getBirthDate() {
		return this.birthDate;
	}

	public int getAge() {
		return this.age;
	}

	public String getAgetype() {
		return this.agetype;
	}

	public char getSex() {
		return this.sex;
	}

	public String getAddress() {
		return this.address;
	}

	public String getCity() {
		return this.city;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public String getNote() {
		return this.note;
	}

	public char getMother() {
		return this.mother;
	}

	public char getFather() {
		return this.father;
	}

	public String getBloodType() {
		return this.bloodType;
	}

	public char getHasInsurance() {
		return this.hasInsurance;
	}

	public char getParentTogether() {
		return this.parentTogether;
	}

	public String getTaxCode() {
		return this.taxCode;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setAgetype(String agetype) {
		this.agetype = agetype;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setNextKin(String nextKin) {
		this.nextKin = nextKin;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setMother(char mother) {
		this.mother = mother;
	}

	public void setFather(char father) {
		this.father = father;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public void setHasInsurance(char hasInsurance) {
		this.hasInsurance = hasInsurance;
	}

	public void setParentTogether(char parentTogether) {
		this.parentTogether = parentTogether;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public PatientSTATUS getStatus() {
		return status;
	}

	public void setStatus(PatientSTATUS status) {
		this.status = status;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public byte[] getBlobPhoto() {
		return blobPhoto;
	}

	public void setBlobPhoto(byte[] blobPhoto) {
		this.blobPhoto = blobPhoto;
	}

}
