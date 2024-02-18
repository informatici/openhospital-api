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

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(description = "Class representing a billItem")
public class BillItemsDTO {

	private Integer id;

	@Schema(description = "Bill id", example = "")
	private Integer billId;

	@Schema(description = "Check if it is a price", example = "true")
	private boolean isPrice;

	@NotNull
	@Schema(description = "The price Id", example = "104")
	private String priceId;

	@NotNull
	@Schema(description = "Item description", example = "Acetone 99 % 1ltr")
	private String itemDescription;

	@NotNull
	@Schema(description = "Item amount", example = "1000")
	private double itemAmount;

	@NotNull
	@Schema(description = "Item quantity", example = "1")
	private int itemQuantity;

	@NotNull
	@Schema(description = "Item display code", example = "Acetone")
	private String itemDisplayCode;

	@NotNull
	@Schema(description = "Item id", example = "3")
	private String itemId;

	private volatile int hashCode;

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getHashCode() {
		return hashCode;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getBillId() {
		return this.billId;
	}

	public boolean isPrice() {
		return this.isPrice;
	}

	public String getPriceId() {
		return this.priceId;
	}

	public String getItemDescription() {
		return this.itemDescription;
	}

	public double getItemAmount() {
		return this.itemAmount;
	}

	public int getItemQuantity() {
		return this.itemQuantity;
	}

	public String getItemDisplayCode() {
		return this.itemDisplayCode;
	}

	public String getItemId() {
		return this.itemId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setBillId(Integer billId) {
		this.billId = billId;
	}

	public void setPrice(boolean isPrice) {
		this.isPrice = isPrice;
	}

	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public void setItemAmount(double itemAmount) {
		this.itemAmount = itemAmount;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public void setItemDisplayCode(String itemDisplayCode) {
		this.itemDisplayCode = itemDisplayCode;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
