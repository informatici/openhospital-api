package org.isf.distype.mapper;

import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.model.DiseaseType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class DiseaseTypeMapper extends GenericMapper<DiseaseType, DiseaseTypeDTO> {
	public DiseaseTypeMapper() {
		super(DiseaseType.class, DiseaseTypeDTO.class);
	}
}