package org.isf.agetype.mapper;

import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.model.AgeType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class AgeTypeMapper extends GenericMapper<AgeType, AgeTypeDTO> {
	public AgeTypeMapper() {
		super(AgeType.class, AgeTypeDTO.class);
	}
}