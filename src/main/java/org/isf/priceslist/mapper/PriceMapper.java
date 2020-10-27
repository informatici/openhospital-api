package org.isf.priceslist.mapper;

import org.isf.priceslist.dto.PriceDTO;
import org.isf.priceslist.model.Price;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PriceMapper extends GenericMapper<Price, PriceDTO> {
	public PriceMapper() {
		super(Price.class, PriceDTO.class);
	}
}