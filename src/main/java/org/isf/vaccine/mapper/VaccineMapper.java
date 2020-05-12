package org.isf.vaccine.mapper;

import org.isf.shared.GenericMapper;
import org.isf.vaccine.dto.VaccineDTO;
import org.isf.vaccine.model.Vaccine;
import org.springframework.stereotype.Component;

@Component
public class VaccineMapper extends GenericMapper<Vaccine, VaccineDTO> {
	public VaccineMapper() {
		super(Vaccine.class, VaccineDTO.class);
	}
}