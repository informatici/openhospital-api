package org.isf.medicalstock.mapper;

import org.isf.medicalstock.dto.MovementDTO;
import org.isf.medicalstock.model.Movement;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper extends GenericMapper<Movement, MovementDTO> {

	public MovementMapper() {
		super(Movement.class, MovementDTO.class);
	}
	
}
