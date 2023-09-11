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
package org.isf.medicalstockward.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.isf.medical.dto.MedicalDTO;
import org.isf.patient.dto.PatientDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class MovementWardDTO {

	@Schema(description = "The movement ward's code", example = "1")
	private int code;

	@NotNull
	@Schema(description = "The ward")
	private WardDTO ward;

	@NotNull
	@Schema(description = "The movement ward's date", example = "2020-06-07", format = "LocalDate")
	private LocalDate date;

	@NotNull
	@Schema(description = "Indicates if the movement is associated to a patient or no ", example = "false")
	private boolean isPatient;

	@Schema(description = "The patient in case the movement is associated to a patient")
	private PatientDTO patient;

	@Schema(description = "The patient's age in case the movement is associated to a patient", example = "21")
	private int age;

	@Schema(description = "The patient's weight in case the movement is associated to a patient", example = "75")
	private float weight;

	@NotNull
	@Schema(description = "The description of the movement", example = "stock transfer from pharmacy to laboratory")
	private String description;

	@Schema(description = "The medical concerned by the movement")
	private MedicalDTO medical;

	@NotNull
	@Schema(description = "The quantity of the medical concerned by the movement", example = "145")
	private Double quantity;

	@NotNull
	@Schema(description = "The measure's unit of the medical concerned by the movement", example = "pct")
	private String units;

	@Schema(description = "The ward to which the movement is done")
	private WardDTO wardTo;

	@Schema(description = "The ward from which the movement is done")
	private WardDTO wardFrom;

	public MovementWardDTO() {
	}

	public MovementWardDTO(int code, WardDTO ward, LocalDate date, boolean isPatient, PatientDTO patient, int age,
			float weight, String description, MedicalDTO medical, Double quantity, String units, WardDTO wardTo,
			WardDTO wardFrom) {
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
		return this.code;
	}

	public WardDTO getWard() {
		return this.ward;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public boolean isPatient() {
		return this.isPatient;
	}

	public PatientDTO getPatient() {
		return this.patient;
	}

	public int getAge() {
		return this.age;
	}

	public float getWeight() {
		return this.weight;
	}

	public String getDescription() {
		return this.description;
	}

	public MedicalDTO getMedical() {
		return this.medical;
	}

	public Double getQuantity() {
		return this.quantity;
	}

	public String getUnits() {
		return this.units;
	}

	public WardDTO getWardTo() {
		return this.wardTo;
	}

	public WardDTO getWardFrom() {
		return this.wardFrom;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setPatient(boolean isPatient) {
		this.isPatient = isPatient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMedical(MedicalDTO medical) {
		this.medical = medical;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public void setWardTo(WardDTO wardTo) {
		this.wardTo = wardTo;
	}

	public void setWardFrom(WardDTO wardFrom) {
		this.wardFrom = wardFrom;
	}
}
