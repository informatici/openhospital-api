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
package org.isf.lab.dto;

public class LaboratoryRowDTO {

    private Integer code;

    private LaboratoryDTO laboratory;

    private String description;

    public LaboratoryRowDTO() {
    }

    public LaboratoryRowDTO(String description, LaboratoryDTO labDTO) {
        this.description = description;
        this.laboratory = labDTO;
    }

	public Integer getCode() {
		return this.code;
	}

	public LaboratoryDTO getLaboratory() {
		return this.laboratory;
	}

	public String getDescription() {
		return this.description;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setLaboratory(LaboratoryDTO laboratory) {
		this.laboratory = laboratory;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
