/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.vaccine.dto;

import javax.validation.constraints.NotNull;

import org.isf.vactype.dto.VaccineTypeDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a vaccine")
public class VaccineDTO {

	@NotNull
	@ApiModelProperty(notes = "Code of the vaccine", example = "1", position = 1)
	private String code;

	@NotNull
	@ApiModelProperty(notes = "Description of the vaccine", example = "BCG", position = 2)
	private String description;

	@NotNull
	@ApiModelProperty(notes = "Type of the vaccine", position = 3)
	private VaccineTypeDTO vaccineType;

	@ApiModelProperty(notes = "lock", example = "0")
	private int lock;

	@Override
	public String toString() {
		return "VaccineDTO{" + "code='" + code + '\'' + ", description='" + description + '\'' + ", vaccineType=" + vaccineType + '}';
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public VaccineTypeDTO getVaccineType() {
		return this.vaccineType;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setVaccineType(VaccineTypeDTO vaccineType) {
		this.vaccineType = vaccineType;
	}
}
