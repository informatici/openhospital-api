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
package org.isf.priceslist.dto;

import javax.validation.constraints.NotNull;

import org.isf.priceslist.model.PriceList;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(description = "Class representing a prices")
public class PriceDTO {

	private int id;

	@NotNull
	@Schema(description = "The price list")
	private PriceList list;

	@NotNull
	@Schema(description = "The group")
	private String group;

	@NotNull
	@Schema(description = "The item name", maxLength = 10)
	private String item;

	@NotNull
	@Schema(description = "The description", maxLength = 100)
	private String description;

	@NotNull
	@Schema(description = "Price", example = "1500")
	private Double price;

	private boolean editable;

	private int hashCode;

	@Schema(accessMode = AccessMode.READ_ONLY)
	public boolean isEditable() {
		return editable;
	}

	@Override
	public String toString() {
		return description;
	}

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getHashCode() {
		return hashCode;
	}

	public int getId() {
		return this.id;
	}

	public PriceList getList() {
		return this.list;
	}

	public String getGroup() {
		return this.group;
	}

	public String getItem() {
		return this.item;
	}

	public String getDescription() {
		return this.description;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setList(PriceList list) {
		this.list = list;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
