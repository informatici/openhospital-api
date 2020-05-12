package org.isf.admission.mapper;

import org.isf.admission.dto.AdmittedPatientDTO;
import org.isf.admission.model.AdmittedPatient;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class AdmittedPatientMapper extends GenericMapper<AdmittedPatient, AdmittedPatientDTO> {
	public AdmittedPatientMapper() {
		super(AdmittedPatient.class, AdmittedPatientDTO.class);
	}
}