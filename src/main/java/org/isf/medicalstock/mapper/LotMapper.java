package org.isf.medicalstock.mapper;

import org.isf.medicalstock.dto.LotDTO;
import org.isf.medicalstock.model.Lot;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class LotMapper extends GenericMapper<Lot, LotDTO> {

	public LotMapper() {
		super(Lot.class, LotDTO.class);
	}
	
}
