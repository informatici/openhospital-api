package org.isf.medical.mapper;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medicals.model.Medical;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MedicalMapper extends GenericMapper<Medical, MedicalDTO> {

	public MedicalMapper() {
		super(Medical.class, MedicalDTO.class);
	}

}
