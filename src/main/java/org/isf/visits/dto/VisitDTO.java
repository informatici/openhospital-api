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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.visits.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Class representing a vaccine type")
public class VisitDTO {

	@Schema(description = "The visit's ID")
	private int visitID;

	@NotNull
	@Schema(description = "Patient related to visitor")
	PatientDTO patient;

	@NotNull
	@Schema(description = "Date of the visit", example = "2020-03-19T14:58:00", type = "string")
	private LocalDateTime date;

	@Schema(description = "Note of the visit", maxLength = 65535)
	private String note;

	@Schema(description = "Sms of the visit")
	private boolean sms;

	@Schema(description = "Ward of the visit")
	private WardDTO ward;

	@Schema(description = "Duration of the visit")
	private Integer duration;

	@Schema(description = "Service done during the visit", maxLength = 45)
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

	public WardDTO getWard() {
		return ward;
	}

	public void setWard(WardDTO ward) {
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
		return "VisitDTO{" + ", patient=" + patient + ", date=" + date + ", note='" + note + '\'' + ", sms=" + sms
				+ '}';
	}
}
