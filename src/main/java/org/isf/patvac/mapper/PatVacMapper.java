package org.isf.patvac.mapper;

import org.isf.patvac.dto.PatientVaccineDTO;
import org.isf.patvac.model.PatientVaccine;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PatVacMapper extends GenericMapper<PatientVaccine, PatientVaccineDTO> {
	public PatVacMapper() {
		super(PatientVaccine.class, PatientVaccineDTO.class);
	}
}