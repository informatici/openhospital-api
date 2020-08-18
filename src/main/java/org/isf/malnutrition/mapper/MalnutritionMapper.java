package org.isf.malnutrition.mapper;

import org.isf.malnutrition.dto.MalnutritionDTO;
import org.isf.malnutrition.model.Malnutrition;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MalnutritionMapper extends GenericMapper<Malnutrition, MalnutritionDTO> {

	public MalnutritionMapper() {
		super(Malnutrition.class, MalnutritionDTO.class);
	}

}
