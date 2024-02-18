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
package org.isf.operation.dto;

import javax.validation.constraints.NotNull;

import org.isf.opetype.dto.OperationTypeDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class OperationDTO {

	@NotNull
	@Schema(description = "The code of operation", maxLength = 10)
	private String code;

	@NotNull
	@Schema(description = "The operation description", maxLength = 50)
	private String description;

	@NotNull
	@Schema(description = "The operation type")
	private OperationTypeDTO type;

	@NotNull
	@Schema(description = "The operation major")
	private Integer major;

	@Schema(description = "Lock", example = "0")
	private int lock;

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

	public OperationTypeDTO getType() {
		return this.type;
	}

	public Integer getMajor() {
		return this.major;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(OperationTypeDTO type) {
		this.type = type;
	}

	public void setMajor(Integer major) {
		this.major = major;
	}

}
