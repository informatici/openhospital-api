package org.isf.encounter.dto;

import org.isf.encounter.model.EncounterStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class EncounterDTO {

	@NotNull
	@Schema(description = "Code of the encounter", example = "123")
	private String code;

	@NotNull
	@Schema(description = "Status of encounter", example = "OPEN")
	private EncounterStatus status;
	
	@NotNull
	@Schema(description = "Patient id", example = "45")
	private Integer patientCode;

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

	public Integer getPatientCode() {
		return patientCode;
	}

	public void setPatientCode(Integer patientCode) {
		this.patientCode = patientCode;
	}

	public EncounterDTO() {}
}
