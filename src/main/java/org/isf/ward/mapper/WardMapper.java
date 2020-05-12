package org.isf.ward.mapper;

import org.isf.shared.GenericMapper;
import org.isf.ward.dto.WardDTO;
import org.isf.ward.model.Ward;
import org.springframework.stereotype.Component;

@Component
public class WardMapper extends GenericMapper<Ward, WardDTO> {
	public WardMapper() {
		super(Ward.class, WardDTO.class);
	}
}