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
package org.isf.visits.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a vaccine type")
public class VisitDTO {

    private int visitID;

    @NotNull
    @ApiModelProperty(notes = "Patient related to visitor", position = 1)
    PatientDTO patient;

    @NotNull
    @ApiModelProperty(notes = "Date of the visit", position = 2)
    private LocalDateTime date;

    @ApiModelProperty(notes = "Note of the visit", position = 3)
    private String note;

    @ApiModelProperty(notes = "Sms of the visit", position = 4)
    private boolean sms;

    @ApiModelProperty(hidden= true)
    public int getVisitID() {
        return visitID;
    }

	public PatientDTO getPatient() {
		return this.patient;
	}

	public LocalDateTime getDate() {
		return this.date;
	}

	public String getNote() {
		return this.note;
	}

	public boolean isSms() {
		return this.sms;
	}

	public void setVisitID(int visitID) {
		this.visitID = visitID;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}

  @Override
	public String toString() {
		return "VisitDTO{" +
                ", patient=" + patient +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", sms=" + sms +
                '}';
  }
}
