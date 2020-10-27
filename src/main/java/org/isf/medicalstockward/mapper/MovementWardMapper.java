package org.isf.medicalstockward.mapper;

import org.isf.medicalstockward.dto.MovementWardDTO;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MovementWardMapper extends GenericMapper<MovementWard, MovementWardDTO> {

	public MovementWardMapper() {
		super(MovementWard.class, MovementWardDTO.class);
	}

}
