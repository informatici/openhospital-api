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
package org.isf.medicalstock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class LotDTO {

	@NotNull(message="The code is required")
	@ApiModelProperty(notes="The lot's code", example = "LT001", position = 1)
	private String code;

	@NotNull(message="The preparation date is required")
	@ApiModelProperty(notes="The preparation date", example = "2020-06-24", position = 2)
	private LocalDate preparationDate;

	@NotNull(message="The due date is required")
	@ApiModelProperty(notes="The due date", example = "2021-06-24", position = 3)
	private LocalDate dueDate;

	@ApiModelProperty(notes="The lot's code", example = "750", position = 4)
	private BigDecimal cost;
	
	public LotDTO() {
	}
	
	public LotDTO(String code, LocalDate preparationDate, LocalDate dueDate, BigDecimal cost) {
		this.code = code;
		this.preparationDate = preparationDate;
		this.dueDate = dueDate;
		this.cost = cost;
	}

	public String getCode() {
		return this.code;
	}

	public LocalDate getPreparationDate() {
		return this.preparationDate;
	}

	public LocalDate getDueDate() {
		return this.dueDate;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setPreparationDate(LocalDate preparationDate) {
		this.preparationDate = preparationDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
}
