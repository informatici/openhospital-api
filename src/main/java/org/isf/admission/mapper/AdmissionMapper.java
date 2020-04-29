package org.isf.admission.mapper;

import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.model.Admission;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class AdmissionMapper extends GenericMapper<Admission, AdmissionDTO> {
	public AdmissionMapper() {
		super(Admission.class, AdmissionDTO.class);
	}
}