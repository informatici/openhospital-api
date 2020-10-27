package org.isf.supplier.mapper;

import org.isf.shared.GenericMapper;
import org.isf.supplier.dto.SupplierDTO;
import org.isf.supplier.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper extends GenericMapper<Supplier, SupplierDTO> {
	public SupplierMapper() {
		super(Supplier.class, SupplierDTO.class);
	}
}
