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
package org.isf.accounting.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class FullBillDTO {

	@NotNull
	@Schema(description = "Bill element")
	private BillDTO bill;

	@NotNull
	@Schema(description = "List of bill items elements")
	private List<BillItemsDTO> billItems;

	@Schema(description = "List of bill payments elements")
	private List<BillPaymentsDTO> billPayments;

	public BillDTO getBill() {
		return this.bill;
	}

	public List<BillItemsDTO> getBillItems() {
		return this.billItems;
	}

	public List<BillPaymentsDTO> getBillPayments() {
		return this.billPayments;
	}

	public void setBill(BillDTO bill) {
		this.bill = bill;
	}

	public void setBillItems(List<BillItemsDTO> billItems) {
		this.billItems = billItems;
	}

	public void setBillPayments(List<BillPaymentsDTO> billPayments) {
		this.billPayments = billPayments;
	}
}
