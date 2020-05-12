package org.isf.dlvrrestype.mapper;

import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class DeliveryResultTypeMapper extends GenericMapper<DeliveryResultType, DeliveryResultTypeDTO> {
	public DeliveryResultTypeMapper() {
		super(DeliveryResultType.class, DeliveryResultTypeDTO.class);
	}
}