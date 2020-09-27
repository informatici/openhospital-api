package org.isf.priceslist.mapper;

import org.isf.priceslist.dto.PriceListDTO;
import org.isf.priceslist.model.PriceList;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PriceListMapper extends GenericMapper<PriceList, PriceListDTO> {
	public PriceListMapper() {
		super(PriceList.class, PriceListDTO.class);
	}
}