package org.isf.dlvrtype.mapper;

import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class DeliveryTypeMapper extends GenericMapper<DeliveryType, DeliveryTypeDTO> {
	public DeliveryTypeMapper() {
		super(DeliveryType.class, DeliveryTypeDTO.class);
	}
}