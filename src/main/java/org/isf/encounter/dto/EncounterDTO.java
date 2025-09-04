/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.encounter.dto;

import org.isf.encounter.model.EncounterStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.isf.patient.dto.PatientDTO;

import java.time.LocalDateTime;

public class EncounterDTO {

	@Schema(description = "id of the encounter", example = "13")
	private Integer id;

	@NotNull
	@Schema(description = "Code of the encounter", example = "123")
	private String code;

	@Schema(description = "Status of encounter", example = "OPEN")
	private EncounterStatus status;

	@NotNull
	@Schema(description = "Patient")
	private PatientDTO patient;

	@Schema(description = "Encounter date", example = "2025-08-26 16:15:58")
	private LocalDateTime performedAt;

	@Schema(description = "Closed date", example = "2025-08-26 16:15:58")
	private LocalDateTime closedAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public EncounterStatus getStatus() {
		return status;
	}
	
	public void setStatus(EncounterStatus status) {
		this.status = status;
	}

	public PatientDTO getPatient() {
		return patient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public LocalDateTime getPerformedAt() {
		return performedAt;
	}

	public void setPerformAt(LocalDateTime performedAt) {
		this.performedAt = performedAt;
	}

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}

	public EncounterDTO() {}
}
