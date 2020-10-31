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
package org.isf.medical.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.isf.medtype.dto.MedicalTypeDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalDTO {
	@ApiModelProperty(notes="The id of the medical", example = "1", position = 1)
	private Integer code;
	
	@NotEmpty(message="The product code is required")
	@ApiModelProperty(notes="The product code", example = "PARA", position = 2)
	private String prod_code;
	
	@NotNull(message="The medical type is required")
	@ApiModelProperty(notes="The medical type", position = 3)
	private MedicalTypeDTO type;
	
	@NotEmpty(message="The description of the medical is required")
	@ApiModelProperty(notes="The description of the medical", example = "Paracétamol", position = 4)
	private String description;
	
	@NotNull(message="Initial quantity is required")
	@ApiModelProperty(notes="The initial quantity of the medical", example = "21", position = 5)
	private double initialqty;
	
	@NotNull(message="The number of pieces per packet is required")
	@ApiModelProperty(notes="The number of pieces per packet", example = "100", position = 6)
	private Integer pcsperpck;
	
	@NotNull(message="The input quantity is required")
	@ApiModelProperty(notes="The input quantity of the medical", example = "340", position = 7)
	private double inqty;
	
	@NotNull(message="The out quantity is required")
	@ApiModelProperty(notes="The out quantity of the medical", example = "8", position = 8)
	private double outqty;
	
	@NotNull(message="The min quantity is required")
	@ApiModelProperty(notes="The min quantity of the medical", example = "15", position = 9)
	private double minqty;
	
	public MedicalDTO() { }
	
	/**
	 * Constructor
	 */
	public MedicalDTO(Integer code, MedicalTypeDTO type, String prod_code, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty) {
		this.code = code;
		this.type = type;
		this.prod_code = prod_code;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
	}

}
