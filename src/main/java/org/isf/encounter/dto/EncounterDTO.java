package org.isf.encounter.dto;

import org.isf.encouter.model.EncounterStatus;
import org.isf.patient.dto.PatientDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class EncounterDTO {

	@NotNull
	@Schema(description = "Code of the encounter")
	private String code;

	@NotNull
	@Schema(description = "Status of encounter")
	private EncounterStatus status;
	
	@NotNull
	@Schema(description = "Patient")
	private PatientDTO patient;

	
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


	public EncounterDTO(String code, EncounterStatus status, PatientDTO patient) {
		this.code = code;
		this.status = status;
		this.patient = patient;
	}


	public EncounterDTO() {}
	
}
