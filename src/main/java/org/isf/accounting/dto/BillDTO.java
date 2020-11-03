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
package org.isf.accounting.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Class representing a bill")
public class BillDTO {
	
	private Integer id;
	
	private PatientDTO patient; 
	
	private Integer listId;

	@NotNull
	@ApiModelProperty(notes = "Date of bill creation", example="2020-03-19T14:58:00.000Z", position = 1)
	private Date date;
	
	@NotNull
	@ApiModelProperty(notes = "Date of bill updated", example="2020-03-19T14:58:00.000Z", position = 2)
	private Date update;
	
	@NotNull
	@ApiModelProperty(notes = "boolean which tells if a price list is applied", example="true", position = 3)
	private boolean isList;
	
	@NotNull
	@ApiModelProperty(notes = "Price list name", example="Basic", position = 4)
	private String listName;
	
	@NotNull
	@ApiModelProperty(notes = "Is bill belongs to a patient?", example="true", position = 5)
	private boolean isPatientBill;
	
	@NotNull
	@ApiModelProperty(notes = "patient name", example="Mario Rossi", position = 6)
	private String patName;
	
	@NotNull
	@ApiModelProperty(notes = "Bill status", example="O", position = 7)
	private String status;

	@NotNull
	@ApiModelProperty(notes = "Bill Amount", example="1000", position = 8)
	private Double amount;
	
	@NotNull
	@ApiModelProperty(notes = "Bill balance", example="1500", position = 9)
	private Double balance;
	
	@NotNull
	@ApiModelProperty(notes = "user name who create the bill", example="admin", position = 10)
	private String user;
	
}
