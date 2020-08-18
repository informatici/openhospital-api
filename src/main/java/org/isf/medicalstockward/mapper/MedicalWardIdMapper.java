package org.isf.medicalstockward.mapper;

import org.isf.medicalstockward.dto.MedicalWardIdDTO;
import org.isf.medicalstockward.model.MedicalWardId;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MedicalWardIdMapper extends GenericMapper<MedicalWardId, MedicalWardIdDTO> {

	public MedicalWardIdMapper() {
		super(MedicalWardId.class, MedicalWardIdDTO.class);
	}

}
