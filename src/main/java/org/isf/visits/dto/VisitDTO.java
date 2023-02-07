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
package org.isf.visits.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;
import org.isf.ward.model.Ward;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a vaccine type")
public class VisitDTO {

	@ApiModelProperty(notes = "The visit's ID", position = 1)
	private int visitID;

	@NotNull
	@ApiModelProperty(notes = "Patient related to visitor", position = 2)
	PatientDTO patient;

	@NotNull
	@ApiModelProperty(notes = "Date of the visit", example = "2020-03-19T14:58:00", position = 3)
	private LocalDateTime date;

	@ApiModelProperty(notes = "Note of the visit", position = 4)
	private String note;

	@ApiModelProperty(notes = "Sms of the visit", position = 5)
	private boolean sms;

	@ApiModelProperty(notes = "ward of the visit", position = 6)
	private Ward ward;

	@ApiModelProperty(notes = "duration of the visit", position = 7)
	private Integer duration;

	@ApiModelProperty(notes = "service done during the visit", position = 8)
	private String service;

	// @ApiModelProperty(hidden=true)
	public int getVisitID() {
		return visitID;
	}

	public PatientDTO getPatient() {
		return this.patient;
	}

	public LocalDateTime getDate() {
		return this.date;
	}

	public Ward getWard() {
		return ward;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
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
		return "VisitDTO{" + ", patient=" + patient + ", date=" + date + ", note='" + note + '\'' + ", sms=" + sms + '}';
	}
}
