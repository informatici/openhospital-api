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
package org.isf.patvac.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;
import org.isf.vaccine.dto.VaccineDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientVaccineDTO
{
	private int code;

	@NotNull
	@ApiModelProperty(notes = "a progr. in year", example="1", position = 1)
	private int progr;

	@NotNull
	@ApiModelProperty(notes = "the vaccine date", position = 2)
	private Date vaccineDate;

	@NotNull
	@ApiModelProperty(notes = "the patient to be vaccine", position = 3)
	private PatientDTO patient;

	@NotNull
	@ApiModelProperty(notes = "the vaccine", position = 4)
	private VaccineDTO vaccine;
	
	private int lock;
	
	private int hashCode = 0;

	@ApiModelProperty(hidden= true)
	public int getLock() {
		return lock;
	}

	@ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

}
