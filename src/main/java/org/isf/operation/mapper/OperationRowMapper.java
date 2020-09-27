package org.isf.operation.mapper;

import org.isf.operation.dto.OperationRowDTO;
import org.isf.operation.model.OperationRow;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class OperationRowMapper extends GenericMapper<OperationRow, OperationRowDTO> {
	public OperationRowMapper() {
		super(OperationRow.class, OperationRowDTO.class);
	}
}