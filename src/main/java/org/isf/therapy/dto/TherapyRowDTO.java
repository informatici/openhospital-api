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
package org.isf.therapy.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TherapyRowDTO {

	@ApiModelProperty(notes="The therapy's ID", example = "1", position = 1)
	private Integer therapyID;

	@NotNull(message="the patient is required")
	@ApiModelProperty(notes="The patient", position = 2)
	PatientDTO patID;

	@NotNull(message="the start date is require")
	@ApiModelProperty(notes="The start date of therapy", example = "2020-07-16", position = 3)
	private Date startDate;

	@NotNull(message="the end date is required")
	@ApiModelProperty(notes="The end date of the therapy", example = "2020-07-30", position = 4)
	private Date endDate;

	@NotNull(message="the medical's ID is required")
	@ApiModelProperty(notes="The ID of the medical concerned by the therapy", example = "1", position = 5)
	private Integer medicalId;

	@NotNull(message="the quantity is required")
	@ApiModelProperty(notes="The quantity of medicals", example = "48", position = 6)
	private Double qty;

	@NotNull(message="the unit's ID is required")
	@ApiModelProperty(notes="The unit's ID", example = "1", position = 7)
	private Integer unitID;

	@NotNull(message="the frequency in day is required")
	@ApiModelProperty(notes="The frequency in day", example = "2", position = 8)
	private Integer freqInDay;

	@NotNull(message="the frequency in period is required")
	@ApiModelProperty(notes="The frequency in period", example = "1", position = 9)
	private Integer freqInPeriod;
	
	@ApiModelProperty(notes="A note for the therapy", example = "Sample note", position = 10)		
	private String note;

	@NotNull(message="the notify flag is required")
	@ApiModelProperty(notes="the notify flag: 1 if the notification need to be activated, 0 otherwise", example = "0", position = 11)	
	private Integer notifyInt;

	@NotNull(message="the sms flag is required")
	@ApiModelProperty(notes="the sms flag: 1 if sms need to be sent to patient, 0 otherwise", example = "0", position = 12)	
	private Integer smsInt;

	public TherapyRowDTO() {
	}

	public TherapyRowDTO(Integer therapyID, PatientDTO patID, Date startDate, Date endDate, Integer medicalId,
			Double qty, Integer unitID, Integer freqInDay, Integer freqInPeriod, String note, Integer notifyInt,
			Integer smsInt) {
		this.therapyID = therapyID;
		this.patID = patID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.medicalId = medicalId;
		this.qty = qty;
		this.unitID = unitID;
		this.freqInDay = freqInDay;
		this.freqInPeriod = freqInPeriod;
		this.note = note;
		this.notifyInt = notifyInt;
		this.smsInt = smsInt;
	}

}
