package org.isf.medstockmovtype.mapper;

import org.isf.medstockmovtype.dto.MovementTypeDTO;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MovementTypeMapper extends GenericMapper<MovementType, MovementTypeDTO> {

	public MovementTypeMapper() {
		super(MovementType.class, MovementTypeDTO.class);
	}

}
