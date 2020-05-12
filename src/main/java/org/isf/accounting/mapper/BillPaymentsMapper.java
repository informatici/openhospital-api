package org.isf.accounting.mapper;

import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.model.BillPayments;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class BillPaymentsMapper extends GenericMapper<BillPayments, BillPaymentsDTO> {
	public BillPaymentsMapper() {
		super(BillPayments.class, BillPaymentsDTO.class);
	}
}