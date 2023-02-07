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
package org.isf.malnutrition.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.admission.dto.AdmissionDTO;

import io.swagger.annotations.ApiModelProperty;

public class MalnutritionDTO {
	
	@ApiModelProperty(notes="The code malnutrition control", example = "1", position = 1)
	private int code;

	@NotNull(message="The date of control is required")
	@ApiModelProperty(notes="The date of this malnutrition control", example = "1979-05-01T11:20:33", position = 2)
	private LocalDateTime dateSupp;

	@ApiModelProperty(notes="The date of the next malnutrition control", example = "1979-05-01T11:20:33", position = 3)
	private LocalDateTime dateConf;

	@NotNull(message="The admission is required")
	@ApiModelProperty(notes="The admission requesting the control", position = 4)
	private AdmissionDTO admission;

	@NotNull(message="The height is required")
	@ApiModelProperty(notes="The height of the patient", example="165", position = 5)
	private float height;

	@NotNull(message="The weight is required")
	@ApiModelProperty(notes="The weight of the patient", example="65", position = 6)
	private float weight;
	
	@ApiModelProperty(notes = "lock", example = "0")
	private int lock;
	
	public MalnutritionDTO() { }
	
	public MalnutritionDTO(int aCode, LocalDateTime aDateSupp,
			LocalDateTime aDateConf, AdmissionDTO anAdmission, float aHeight,
			float aWeight) {
		code = aCode;
		dateSupp = aDateSupp;
		dateConf = aDateConf;
		admission = anAdmission;
		height = aHeight;
		weight = aWeight;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public int getCode() {
		return this.code;
	}

	public LocalDateTime getDateSupp() {
		return this.dateSupp;
	}

	public LocalDateTime getDateConf() {
		return this.dateConf;
	}

	public AdmissionDTO getAdmission() {
		return this.admission;
	}

	public float getHeight() {
		return this.height;
	}

	public float getWeight() {
		return this.weight;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setDateSupp(LocalDateTime dateSupp) {
		this.dateSupp = dateSupp;
	}

	public void setDateConf(LocalDateTime dateConf) {
		this.dateConf = dateConf;
	}

	public void setAdmission(AdmissionDTO admission) {
		this.admission = admission;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

}
