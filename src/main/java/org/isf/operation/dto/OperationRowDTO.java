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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.isf.operation.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.accounting.dto.BillDTO;
import org.isf.admission.dto.AdmissionDTO;
import org.isf.opd.dto.OpdDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

public class OperationRowDTO {

	private int id;

	@NotNull
	@Schema(description = "The operation")
	private OperationDTO operation;

	@NotNull
	@Schema(description = "The presciber of the operation", maxLength = 150)
	private String prescriber;

	@NotNull
	@Schema(description = "The result of the operation", maxLength = 250)
	private String opResult;

	@NotNull
	@Schema(description = "Operation registration date", type = "string")
	private LocalDateTime opDate;

	@Schema(description = "The remark of the operation", maxLength = 250)
	private String remarks;

	@Schema(description = "The admission")
	private AdmissionDTO admission;

	@Schema(description = "The opd")
	private OpdDTO opd;

	@Schema(description = "The bill")
	private BillDTO bill;

	@Schema(description = "The transunit")
	private Float transUnit;

	private int hashCode;

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getHashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		return this.operation.getDescription() + ' ' + this.admission.getUserID();
	}

	public int getId() {
		return this.id;
	}

	public OperationDTO getOperation() {
		return this.operation;
	}

	public String getPrescriber() {
		return this.prescriber;
	}

	public String getOpResult() {
		return this.opResult;
	}

	public LocalDateTime getOpDate() {
		return this.opDate;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public AdmissionDTO getAdmission() {
		return this.admission;
	}

	public OpdDTO getOpd() {
		return this.opd;
	}

	public BillDTO getBill() {
		return this.bill;
	}

	public Float getTransUnit() {
		return this.transUnit;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setOperation(OperationDTO operation) {
		this.operation = operation;
	}

	public void setPrescriber(String prescriber) {
		this.prescriber = prescriber;
	}

	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}

	public void setOpDate(LocalDateTime opDate) {
		this.opDate = opDate;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setAdmission(AdmissionDTO admission) {
		this.admission = admission;
	}

	public void setOpd(OpdDTO opd) {
		this.opd = opd;
	}

	public void setBill(BillDTO bill) {
		this.bill = bill;
	}

	public void setTransUnit(Float transUnit) {
		this.transUnit = transUnit;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
