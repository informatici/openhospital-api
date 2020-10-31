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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.isf.operation.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.accounting.dto.BillDTO;
import org.isf.admission.dto.AdmissionDTO;
import org.isf.opd.dto.OpdDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationRowDTO {

    private int id;

    @NotNull
    private OperationDTO operation;

    @NotNull
    private String prescriber;

    @NotNull
    private String opResult;

    @NotNull
    private Date opDate;

    private String remarks;

    @NotNull
    private AdmissionDTO admission;

    @NotNull
    private OpdDTO opd;

    private BillDTO bill;

    private Float transUnit;
    
    private int hashCode = 0;

    @ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
        return this.operation.getDescription() + " " + this.admission.getUserID();
    }
}
