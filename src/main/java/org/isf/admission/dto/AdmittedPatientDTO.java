package org.isf.admission.dto;

import org.isf.patient.dto.PatientDTO;

public class AdmittedPatientDTO {

	private PatientDTO patient;
	private AdmissionDTO admission;
	public PatientDTO getPatient() {
		return patient;
	}
	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}
	public AdmissionDTO getAdmission() {
		return admission;
	}
	public void setAdmission(AdmissionDTO admission) {
		this.admission = admission;
	}
	
	
	
}
