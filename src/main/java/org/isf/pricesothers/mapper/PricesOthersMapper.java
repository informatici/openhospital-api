package org.isf.pricesothers.mapper;

import org.isf.pricesothers.dto.PricesOthersDTO;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PricesOthersMapper extends GenericMapper<PricesOthers, PricesOthersDTO> {
	public PricesOthersMapper() {
		super(PricesOthers.class, PricesOthersDTO.class);
	}
}