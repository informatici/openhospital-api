package org.isf.conditioning.mapper;

import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.model.Conditioning;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class ConditioningMapper extends GenericMapper<Conditioning, ConditioningDTO> {
	public ConditioningMapper() {
		super(Conditioning.class, ConditioningDTO.class);
	}
}
