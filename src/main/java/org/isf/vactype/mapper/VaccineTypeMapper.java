package org.isf.vactype.mapper;

import org.isf.shared.GenericMapper;
import org.isf.vactype.dto.VaccineTypeDTO;
import org.isf.vactype.model.VaccineType;
import org.springframework.stereotype.Component;

@Component
public class VaccineTypeMapper extends GenericMapper<VaccineType, VaccineTypeDTO> {
	public VaccineTypeMapper() {
		super(VaccineType.class, VaccineTypeDTO.class);
	}
}