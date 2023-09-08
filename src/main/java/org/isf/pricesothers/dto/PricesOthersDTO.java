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
package org.isf.pricesothers.dto;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(description = "Class representing a price others")
public class PricesOthersDTO {
	@Id
	private int id;

	@NotNull
	@Schema(description = "The prices code", example = "PRICES001", maxLength = 10)
	private String code;

	@NotNull
	@Schema(description = "The description", maxLength = 100)
	private String description;

	@NotNull
	private boolean opdInclude;

	@NotNull
	private boolean ipdInclude;

	@NotNull
	private boolean daily;

	private boolean discharge;

	private boolean undefined;

	private int hashCode;

	@Schema(accessMode = AccessMode.READ_ONLY)
	public int getHashCode() {
		return hashCode;
	}

	public int getId() {
		return this.id;
	}

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isOpdInclude() {
		return this.opdInclude;
	}

	public boolean isIpdInclude() {
		return this.ipdInclude;
	}

	public boolean isDaily() {
		return this.daily;
	}

	public boolean isDischarge() {
		return this.discharge;
	}

	public boolean isUndefined() {
		return this.undefined;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOpdInclude(boolean opdInclude) {
		this.opdInclude = opdInclude;
	}

	public void setIpdInclude(boolean ipdInclude) {
		this.ipdInclude = ipdInclude;
	}

	public void setDaily(boolean daily) {
		this.daily = daily;
	}

	public void setDischarge(boolean discharge) {
		this.discharge = discharge;
	}

	public void setUndefined(boolean undefined) {
		this.undefined = undefined;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}