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
package org.isf.supplier.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class SupplierDTO {

	@NotNull(message="supplier's ID is required")
	@ApiModelProperty(notes="The supplier's ID", example = "111", position = 1)
	private Integer supId;
	
	@NotNull(message="supplier's name is required")
	@ApiModelProperty(notes="The supplier's name", example = "Cogefar", position = 2)
	private String supName;
	
	@ApiModelProperty(notes="The supplier's address", example = "25 Rue Ministre, Dschang", position = 3)
	private String supAddress;
	
	@ApiModelProperty(notes="The supplier's tax code", example = "5221", position = 4)
	private String supTaxcode;
	
	@ApiModelProperty(notes="The supplier's phone", example = "+237654120145", position = 5)
	private String supPhone;
	
	@ApiModelProperty(notes="The supplier's fax number", example = "+237654120145", position = 6)
	private String supFax;
	
	@ApiModelProperty(notes="The supplier's e-mail address", example = "suplier@sample.com", position = 7)
	private String supEmail;
	
	@ApiModelProperty(notes="The supplier's notes", example = "", position = 8)
	private String supNote;

	public SupplierDTO() {
	}
	
	public SupplierDTO(Integer supId, String supName, String supAddress, String supTaxcode, String supPhone,
			String supFax, String supEmail, String supNote) {
		this.supId = supId;
		this.supName = supName;
		this.supAddress = supAddress;
		this.supTaxcode = supTaxcode;
		this.supPhone = supPhone;
		this.supFax = supFax;
		this.supEmail = supEmail;
		this.supNote = supNote;
	}

	public Integer getSupId() {
		return this.supId;
	}

	public String getSupName() {
		return this.supName;
	}

	public String getSupAddress() {
		return this.supAddress;
	}

	public String getSupTaxcode() {
		return this.supTaxcode;
	}

	public String getSupPhone() {
		return this.supPhone;
	}

	public String getSupFax() {
		return this.supFax;
	}

	public String getSupEmail() {
		return this.supEmail;
	}

	public String getSupNote() {
		return this.supNote;
	}

	public void setSupId(Integer supId) {
		this.supId = supId;
	}

	public void setSupName(String supName) {
		this.supName = supName;
	}

	public void setSupAddress(String supAddress) {
		this.supAddress = supAddress;
	}

	public void setSupTaxcode(String supTaxcode) {
		this.supTaxcode = supTaxcode;
	}

	public void setSupPhone(String supPhone) {
		this.supPhone = supPhone;
	}

	public void setSupFax(String supFax) {
		this.supFax = supFax;
	}

	public void setSupEmail(String supEmail) {
		this.supEmail = supEmail;
	}

	public void setSupNote(String supNote) {
		this.supNote = supNote;
	}
}
