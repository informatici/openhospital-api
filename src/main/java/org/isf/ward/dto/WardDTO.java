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
package org.isf.ward.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class WardDTO {

	private String code;

	@NotNull
	private String description;

	private String telephone;

	private String fax;

	private String email;

	@NotNull
	private Integer beds;

	@NotNull
	private Integer nurs;

	@NotNull
	private Integer docs;

	@NotNull
	private boolean isPharmacy;

	@NotNull
	private boolean isMale;

	@NotNull
	private boolean isFemale;

	@NotNull
	private int visitDuration;

	@ApiModelProperty(notes = "lock", example = "0")
	private Integer lock;
	
	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public String getFax() {
		return this.fax;
	}

	public String getEmail() {
		return this.email;
	}

	public Integer getBeds() {
		return this.beds;
	}

	public Integer getNurs() {
		return this.nurs;
	}

	public Integer getDocs() {
		return this.docs;
	}

	public boolean isPharmacy() {
		return this.isPharmacy;
	}

	public boolean isMale() {
		return this.isMale;
	}

	public boolean isFemale() {
		return this.isFemale;
	}

	public Integer getVisitDuration() {
		return this.visitDuration;
	}

	public Integer getLock() {
		return this.lock;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setBeds(Integer beds) {
		this.beds = beds;
	}

	public void setNurs(Integer nurs) {
		this.nurs = nurs;
	}

	public void setDocs(Integer docs) {
		this.docs = docs;
	}

	public void setPharmacy(boolean isPharmacy) {
		this.isPharmacy = isPharmacy;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}

	public void setVisitDuration(Integer visitDuration) {
		this.visitDuration = visitDuration;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}
}
