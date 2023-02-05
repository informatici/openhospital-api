/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.disease.dto;

import javax.validation.constraints.NotNull;

import org.isf.distype.dto.DiseaseTypeDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a disease")
public class DiseaseDTO {

	@NotNull
	@ApiModelProperty(notes = "Disease code", example = "99")
	private String code;

	@NotNull
	@ApiModelProperty(notes = "Disease description")
    private String description;

	@NotNull
	@ApiModelProperty(notes = "Disease type")
	private DiseaseTypeDTO diseaseType; 

	@NotNull
	@ApiModelProperty(notes = "indicates whether the disease is an OPD disease", example="true")
	private boolean opdInclude;

	@NotNull
	@ApiModelProperty(notes = "indicates whether the disease is an IPD-IN disease", example="true")
	private boolean ipdInInclude;

	@NotNull
	@ApiModelProperty(notes = "indicates whether the disease is an IPD-OUT disease", example="true")
	private boolean ipdOutInclude;
	
	private int hashCode;
	
	@ApiModelProperty(notes = "lock", example = "0")
	private int lock;
	
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@ApiModelProperty(hidden=true)
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
