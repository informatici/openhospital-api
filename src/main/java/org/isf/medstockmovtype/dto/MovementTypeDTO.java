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
package org.isf.medstockmovtype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class MovementTypeDTO {

	@NotNull
	@ApiModelProperty(notes="Code of the movement type", example = "D", position = 1)
	private String code;
	
	@NotNull
	@ApiModelProperty(notes="Description of the movement type", example = "Damage", position = 2)
	private String description;
	
	@NotNull
	@ApiModelProperty(notes="Type of the movement type", example = "-", position = 3)
	private String type;
	
	public MovementTypeDTO(){}
    
    /**
     * @param code
     * @param description
     * @param type
     */
    public MovementTypeDTO(String code, String description, String type) {
        this.code = code;
        this.description = description;
        this.type = type;
    }

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public String getType() {
		return this.type;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(String type) {
		this.type = type;
	}
}
