package org.isf.medtype.mapper;

import org.isf.medtype.dto.MedicalTypeDTO;
import org.isf.medtype.model.MedicalType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MedicalTypeMapper extends GenericMapper<MedicalType, MedicalTypeDTO> {
	public MedicalTypeMapper() {
		super(MedicalType.class, MedicalTypeDTO.class);
	}
}
