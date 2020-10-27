package org.isf.accounting.mapper;

import org.isf.accounting.dto.BillDTO;
import org.isf.accounting.model.Bill;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class BillMapper extends GenericMapper<Bill, BillDTO> {
	public BillMapper() {
		super(Bill.class, BillDTO.class);
	}
}