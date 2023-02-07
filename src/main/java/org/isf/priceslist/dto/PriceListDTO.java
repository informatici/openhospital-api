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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a price list")
public class PriceListDTO {
	
	private int id;

	@ApiModelProperty(notes = "the price list code", example="LISTE1", position = 1)
	private String code;
	
	@ApiModelProperty(notes = "the name of list", example="default price list", position = 2)
    private String name;
	
	@ApiModelProperty(notes = "the price list description", example="default price list", position = 3)
	private String description;

	@ApiModelProperty(notes = "the currency", example="FCFA", position = 4)
	private String currency;
	
	private int hashCode;

	@ApiModelProperty(hidden=true)
	public int getHashCode() {
		return hashCode;
	}

	public int getId() {
		return this.id;
	}

	public String getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
