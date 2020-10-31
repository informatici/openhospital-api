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

import org.isf.medical.dto.MedicalDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TherapyDTO {

	@ApiModelProperty(notes="The therapy's ID", example = "1", position = 1)
	private Integer therapyID;
	
	@ApiModelProperty(notes="The patient's ID", example = "1", position = 2)
	private Integer patID;
	
	@ApiModelProperty(notes="The dates of the therapy", position = 3)
	private Date[] dates;
	
	@ApiModelProperty(notes="The medical associated to the therapy", position = 4)
	private MedicalDTO medical;
	
	@ApiModelProperty(notes="The quantity of the medical", example = "48", position = 5)
	private Double qty;
	
	@ApiModelProperty(notes="The units", position = 6)
	private String units;
	
	@ApiModelProperty(notes="The frequency in day", example = "2", position = 7)
	private Integer freqInDay;
	
	@ApiModelProperty(notes="A note for the therapy", example = "Sample note", position = 8)
	private String note;
	
	@ApiModelProperty(notes="the notify flag: true if the notification need to be activated, false otherwise", example = "false", position = 9)	
	private boolean notify;
	
	@ApiModelProperty(notes="the sms flag: true if sms need to be sent to patient, false otherwise", example = "false", position = 10)
	private boolean sms;

	public TherapyDTO() {
	}

	public TherapyDTO(Integer therapyID, Integer patID, Date[] dates, MedicalDTO medical, Double qty, String units,
			Integer freqInDay, String note, boolean notify, boolean sms) {
		this.therapyID = therapyID;
		this.patID = patID;
		this.dates = dates;
		this.medical = medical;
		this.qty = qty;
		this.units = units;
		this.freqInDay = freqInDay;
		this.note = note;
		this.notify = notify;
		this.sms = sms;
	}

}
