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

import io.swagger.annotations.ApiModelProperty;

public class OperationRowDTO {

    private int id;

    @NotNull
    private OperationDTO operation;

    @NotNull
    private String prescriber;

    @NotNull
    private String opResult;

    @NotNull
    private LocalDateTime opDate;

    private String remarks;

    private AdmissionDTO admission;

    private OpdDTO opd;

    private BillDTO bill;

    private Float transUnit;
    
    private int hashCode;

    @ApiModelProperty(hidden=true)
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
