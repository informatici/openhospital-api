package org.isf.pregtreattype.mapper;

import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PregnantTreatmentTypeMapper extends GenericMapper<PregnantTreatmentType, PregnantTreatmentTypeDTO> {
	public PregnantTreatmentTypeMapper() {
		super(PregnantTreatmentType.class, PregnantTreatmentTypeDTO.class);
	}
}