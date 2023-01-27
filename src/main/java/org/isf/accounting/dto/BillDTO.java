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
package org.isf.accounting.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a bill")
public class BillDTO {
	
	private Integer id;
	
	private PatientDTO patient; 
	
	private Integer listId;

	@NotNull
	@ApiModelProperty(notes = "Date of bill creation", example="2020-03-19T14:58:00.000Z", position = 1)
	private LocalDateTime date;
	
	@NotNull
	@ApiModelProperty(notes = "Date of bill updated", example="2020-03-19T14:58:00.000Z", position = 2)
	private LocalDateTime update;
	
	@NotNull
	@ApiModelProperty(notes = "boolean which tells if a price list is applied", example="true", position = 3)
	private boolean isList;
	
	@NotNull
	@ApiModelProperty(notes = "Price list name", example="Basic", position = 4)
	private String listName;
	
	@NotNull
	@ApiModelProperty(notes = "Is bill belongs to a patient?", example="true", position = 5)
	private boolean patientTrue;
	
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

	public Integer getId() {
		return this.id;
	}

	public PatientDTO getPatient() {
		return this.patient;
	}

	public Integer getListId() {
		return this.listId;
	}

	public LocalDateTime getDate() {
		return this.date;
	}

	public LocalDateTime getUpdate() {
		return this.update;
	}

	public boolean isList() {
		return this.isList;
	}

	public String getListName() {
		return this.listName;
	}

	public boolean isPatientTrue() {
		return this.patientTrue;
	}

	public String getPatName() {
		return this.patName;
	}

	public String getStatus() {
		return this.status;
	}

	public Double getAmount() {
		return this.amount;
	}

	public Double getBalance() {
		return this.balance;
	}

	public String getUser() {
		return this.user;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setUpdate(LocalDateTime update) {
		this.update = update;
	}

	public void setList(boolean isList) {
		this.isList = isList;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public void setPatientTrue(boolean isPatientBill) {
		this.patientTrue = isPatientBill;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
