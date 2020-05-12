package org.isf.patient.mapper;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.model.Patient;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper extends GenericMapper<Patient, PatientDTO> {
	public PatientMapper() {
		super(Patient.class, PatientDTO.class);
	}
}