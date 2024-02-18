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
package org.isf.therapy.dto;

import java.time.LocalDateTime;

import org.isf.medical.dto.MedicalDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class TherapyDTO {

	@Schema(description = "The therapy's ID", example = "1")
	private Integer therapyID;

	@Schema(description = "The patient's ID", example = "1")
	private Integer patID;

	@Schema(description = "The dates of the therapy", type = "string[]", example = "[\"2022-01-01T10:00:00\", \"2022-01-02T15:30:00\"]")
	private LocalDateTime[] dates;

	@Schema(description = "The medical associated to the therapy")
	private MedicalDTO medical;

	@Schema(description = "The quantity of the medical", example = "48")
	private Double qty;

	@Schema(description = "The units")
	private String units;

	@Schema(description = "The frequency in day", example = "2")
	private Integer freqInDay;

	@Schema(description = "A note for the therapy", example = "Sample note", maxLength = 65535)
	private String note;

	@Schema(description = "The notify flag: true if the notification need to be activated, false otherwise", example = "false")
	private boolean notify;

	@Schema(description = "The sms flag: true if sms need to be sent to patient, false otherwise", example = "false")
	private boolean sms;

	public TherapyDTO() {
	}

	public TherapyDTO(Integer therapyID, Integer patID, LocalDateTime[] dates, MedicalDTO medical, Double qty,
			String units, Integer freqInDay, String note, boolean notify, boolean sms) {
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

	public Integer getTherapyID() {
		return this.therapyID;
	}

	public Integer getPatID() {
		return this.patID;
	}

	public LocalDateTime[] getDates() {
		return this.dates;
	}

	public MedicalDTO getMedical() {
		return this.medical;
	}

	public Double getQty() {
		return this.qty;
	}

	public String getUnits() {
		return this.units;
	}

	public Integer getFreqInDay() {
		return this.freqInDay;
	}

	public String getNote() {
		return this.note;
	}

	public boolean isNotify() {
		return this.notify;
	}

	public boolean isSms() {
		return this.sms;
	}

	public void setTherapyID(Integer therapyID) {
		this.therapyID = therapyID;
	}

	public void setPatID(Integer patID) {
		this.patID = patID;
	}

	public void setDates(LocalDateTime[] dates) {
		this.dates = dates;
	}

	public void setMedical(MedicalDTO medical) {
		this.medical = medical;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public void setFreqInDay(Integer freqInDay) {
		this.freqInDay = freqInDay;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}
}
