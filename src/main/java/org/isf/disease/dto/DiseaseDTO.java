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
package org.isf.disease.dto;

import javax.validation.constraints.NotNull;

import org.isf.distype.dto.DiseaseTypeDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(description = "Class representing a disease")
public class DiseaseDTO {

	@NotNull
	@Schema(description = "Disease code", example = "99", maxLength = 10)
	private String code;

	@NotNull
	@Schema(description = "Disease description", maxLength = 50)
	private String description;

	@NotNull
	@Schema(description = "Disease type")
	private DiseaseTypeDTO diseaseType;

	@NotNull
	@Schema(description = "Indicates whether the disease is an OPD disease", example = "true")
	private boolean opdInclude;

	@NotNull
	@Schema(description = "Indicates whether the disease is an IPD-IN disease", example = "true")
	private boolean ipdInInclude;

	@NotNull
	@Schema(description = "Indicates whether the disease is an IPD-OUT disease", example = "true")
	private boolean ipdOutInclude;

	private int hashCode;

	@Schema(description = "Lock", example = "0")
	private int lock;

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getHashCode() {
		return hashCode;
	}

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public DiseaseTypeDTO getDiseaseType() {
		return this.diseaseType;
	}

	public boolean isOpdInclude() {
		return this.opdInclude;
	}

	public boolean isIpdInInclude() {
		return this.ipdInInclude;
	}

	public boolean isIpdOutInclude() {
		return this.ipdOutInclude;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDiseaseType(DiseaseTypeDTO diseaseType) {
		this.diseaseType = diseaseType;
	}

	public void setOpdInclude(boolean opdInclude) {
		this.opdInclude = opdInclude;
	}

	public void setIpdInInclude(boolean ipdInInclude) {
		this.ipdInInclude = ipdInInclude;
	}

	public void setIpdOutInclude(boolean ipdOutInclude) {
		this.ipdOutInclude = ipdOutInclude;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

}
