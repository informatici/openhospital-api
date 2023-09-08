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
package org.isf.vaccine.dto;

import javax.validation.constraints.NotNull;

import org.isf.vactype.dto.VaccineTypeDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Class representing a vaccine")
public class VaccineDTO {

	@NotNull
	@Schema(description = "Code of the vaccine", example = "1", maxLength = 10)
	private String code;

	@NotNull
	@Schema(description = "Description of the vaccine", example = "BCG", maxLength = 50)
	private String description;

	@NotNull
	@Schema(description = "Type of the vaccine")
	private VaccineTypeDTO vaccineType;

	@Schema(description = "Lock", example = "0")
	private int lock;

	@Override
	public String toString() {
		return "VaccineDTO{" + "code='" + code + '\'' + ", description='" + description + '\'' + ", vaccineType="
				+ vaccineType + '}';
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
