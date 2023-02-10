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
package org.isf.priceslist.dto;

import javax.validation.constraints.NotNull;

import org.isf.priceslist.model.PriceList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a prices")
public class PriceDTO
{

	private int id;

	@NotNull
	@ApiModelProperty(notes = "the price list", position = 1)
    private PriceList list;

	@NotNull
	@ApiModelProperty(notes = "the group", position = 2)
    private String group;

	@NotNull
	@ApiModelProperty(notes = "the item name", position = 3)
    private String item;

	@NotNull
	@ApiModelProperty(notes = "the description", position = 4)
    private String description;

	@NotNull
	@ApiModelProperty(notes = "price", example="1500", position = 4)
    private Double price; 
	
    private boolean editable;
	
	private int hashCode;

	@ApiModelProperty(hidden=true)
	public boolean isEditable() {
		return editable;
	}

	@Override
	public String toString() {
		return description;
	}
	
	@ApiModelProperty(hidden=true)
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
