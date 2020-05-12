package org.isf.disease.mapper;

import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.model.Disease;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class DiseaseMapper extends GenericMapper<Disease, DiseaseDTO> {
	public DiseaseMapper() {
		super(Disease.class, DiseaseDTO.class);
	}
}