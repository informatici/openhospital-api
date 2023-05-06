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
package org.isf.patconsensus.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a patient consensus")
public class PatientConsensusDTO {

	@ApiModelProperty(notes = "consensus flag", position = 1)
	private boolean consensusFlag;

	@ApiModelProperty(notes = "administrative flag", position = 2)
	private boolean administrativeFlag;

	@ApiModelProperty(notes = "service flag", position = 3)
	private boolean serviceFlag;

	@NotNull
	@ApiModelProperty(notes = "patient id", position = 4)
	private Integer patientId;

	public boolean isConsensusFlag() {
		return consensusFlag;
	}

	public void setConsensusFlag(boolean consensusFlag) {
		this.consensusFlag = consensusFlag;
	}

	public boolean isAdministrativeFlag() {
		return administrativeFlag;
	}

	public void setAdministrativeFlag(boolean administrativeFlag) {
		this.administrativeFlag = administrativeFlag;
	}

	public boolean isServiceFlag() {
		return serviceFlag;
	}

	public void setServiceFlag(boolean serviceFlag) {
		this.serviceFlag = serviceFlag;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

}
