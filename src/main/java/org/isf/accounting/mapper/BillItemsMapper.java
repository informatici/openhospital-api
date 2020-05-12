package org.isf.accounting.mapper;

import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.model.BillItems;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class BillItemsMapper extends GenericMapper<BillItems, BillItemsDTO> {
	public BillItemsMapper() {
		super(BillItems.class, BillItemsDTO.class);
	}
}