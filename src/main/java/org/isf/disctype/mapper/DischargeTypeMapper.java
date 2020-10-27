package org.isf.disctype.mapper;

import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.model.DischargeType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class DischargeTypeMapper extends GenericMapper<DischargeType, DischargeTypeDTO> {
	public DischargeTypeMapper() {
		super(DischargeType.class, DischargeTypeDTO.class);
	}
}