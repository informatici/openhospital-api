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

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medstockmovtype.dto.MovementTypeDTO;
import org.isf.supplier.dto.SupplierDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.annotations.ApiModelProperty;

public class MovementDTO {

	@ApiModelProperty(notes="The movement code", example = "1", position = 1)
	private int code;

	@NotNull(message="The medical is required")
	@ApiModelProperty(notes="The related medical", position = 2)
	private MedicalDTO medical;

	@NotNull(message="The movement type is required")
	@ApiModelProperty(notes="The movement type", position = 3)
	private MovementTypeDTO type;

	@ApiModelProperty(notes="The target ward", position = 4)
	private WardDTO ward;

	@ApiModelProperty(notes="The lot", position = 5)
	private LotDTO lot;

	@NotNull(message="the movement's date is required")
	@ApiModelProperty(notes="The movement date", example = "2020-06-24", position = 6)
	private LocalDate date;

	@NotNull(message="the movement's medical quantity is required")
	@ApiModelProperty(notes="The movement's medical quantity", example = "50", position = 7)
	private int quantity;

	@ApiModelProperty(notes="The movement's supplier", position = 8)
	private SupplierDTO supplier;
	
	@NotNull(message="the movement reference is required")
	@ApiModelProperty(notes="The movement reference", example = "MVN152445", position = 9)
	private String refNo;
	
	public MovementDTO() {
	}
	
	public MovementDTO(int code, MedicalDTO medical, MovementTypeDTO type, WardDTO ward, LotDTO lot, LocalDate date,
			int quantity, SupplierDTO supplier, String refNo) {
		this.code = code;
		this.medical = medical;
		this.type = type;
		this.ward = ward;
		this.lot = lot;
		this.date = date;
		this.quantity = quantity;
		this.supplier = supplier;
		this.refNo = refNo;
	}

	public int getCode() {
		return this.code;
	}

	public MedicalDTO getMedical() {
		return this.medical;
	}

	public MovementTypeDTO getType() {
		return this.type;
	}

	public WardDTO getWard() {
		return this.ward;
	}

	public LotDTO getLot() {
		return this.lot;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public SupplierDTO getSupplier() {
		return this.supplier;
	}

	public String getRefNo() {
		return this.refNo;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMedical(MedicalDTO medical) {
		this.medical = medical;
	}

	public void setType(MovementTypeDTO type) {
		this.type = type;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

	public void setLot(LotDTO lot) {
		this.lot = lot;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setSupplier(SupplierDTO supplier) {
		this.supplier = supplier;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
}
