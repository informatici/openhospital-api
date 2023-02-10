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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.accounting.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a billPayment")
public class BillPaymentsDTO {
	
	private Integer id;

	@NotNull
	@ApiModelProperty(notes = "Bill id", example="", position = 1)
	private Integer billId;

	@NotNull
	@ApiModelProperty(notes = "date of payment", example="2020-03-19T14:58:00.000Z", position = 2)
	private LocalDateTime date;

	@NotNull
	@ApiModelProperty(notes = "the payment amount", example="500", position = 3)
	private double amount;

	@NotNull
	@ApiModelProperty(notes = "the current user", example="admin", position = 4)
	private String user;

	private volatile int hashCode;

	public Integer getId() {
		return this.id;
	}

	public Integer getBillId() {
		return this.billId;
	}

	public LocalDateTime getDate() {
		return this.date;
	}

	public double getAmount() {
		return this.amount;
	}

	public String getUser() {
		return this.user;
	}

	@ApiModelProperty(hidden=true)
	public int getHashCode() {
		return hashCode;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public void setBillId(Integer billId) {
		this.billId = billId;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
