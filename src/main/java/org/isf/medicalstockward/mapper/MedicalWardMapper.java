package org.isf.medicalstockward.mapper;

import org.isf.medicalstockward.dto.MedicalWardDTO;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MedicalWardMapper extends GenericMapper<MedicalWard, MedicalWardDTO> {

	public MedicalWardMapper() {
		super(MedicalWard.class, MedicalWardDTO.class);
	}
	
}
