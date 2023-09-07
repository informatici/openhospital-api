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
package org.isf.lab.dto;

import java.time.LocalDateTime;

import org.isf.exam.dto.ExamDTO;
import org.isf.lab.model.LaboratoryStatus;
import org.isf.patient.dto.PatientSTATUS;
import org.isf.utils.time.TimeTools;

import com.drew.lang.annotations.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class LaboratoryDTO {

	@Schema(description = "Laboratory Code")
	private Integer code;

	@Schema(description = "Laboratory Material", example = "Blood")
	private String material;

	@Schema(description = "Laboratory Exam")
	private ExamDTO exam;

	@Schema(description = "Laboratory Registration Date", type = "string")
	private LocalDateTime registrationDate;

	@NotNull
	@Schema(description = "Laboratory Exam Date", type = "string")
	private LocalDateTime labDate;

	@Schema(description = "Laboratory Result")
	private String result;

	@Schema(description = "Lock", example = "0")
	private int lock;

	@Schema(description = "Laboratory Note", example = "Note by laboratorist", maxLength = 255)
	private String note;

	@Schema(description = "Laboratory Patient Code")
	private Integer patientCode;

	@Schema(description = "Laboratory Patient Name")
	private String patName;

	@Schema(description = "Laboratory Patient InOut", example = "0")
	private PatientSTATUS inOutPatient;

	@Schema(description = "Laboratory Patient Age")
	private Integer age;

	@Schema(description = "Laboratory Patient Sex", example = "M")
	private String sex;

	@Schema(description = "Laboratory status", example = "DRAFT")
	private LaboratoryStatus status;

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public Integer getCode() {
		return this.code;
	}

	public String getMaterial() {
		return this.material;
	}

	public ExamDTO getExam() {
		return this.exam;
	}

	public LocalDateTime getRegistrationDate() {
		return this.registrationDate;
	}

	public LocalDateTime getLabDate() {
		return labDate;
	}

	public String getResult() {
		return this.result;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getPatientCode() {
		return this.patientCode;
	}

	public String getPatName() {
		return this.patName;
	}

	public PatientSTATUS getInOutPatient() {
		return this.inOutPatient;
	}

	public Integer getAge() {
		return this.age;
	}

	public String getSex() {
		return this.sex;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public void setExam(ExamDTO exam) {
		this.exam = exam;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public void setLabDate(LocalDateTime aDate) {
		labDate = TimeTools.truncateToSeconds(aDate);
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setPatientCode(Integer patientCode) {
		this.patientCode = patientCode;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setInOutPatient(PatientSTATUS inOutPatient) {
		this.inOutPatient = inOutPatient;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public LaboratoryStatus getStatus() {
		return status;
	}

	public void setStatus(LaboratoryStatus status) {
		this.status = status;
	}
}
