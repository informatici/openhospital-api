package org.isf.operation.mapper;

import org.isf.operation.dto.OperationDTO;
import org.isf.operation.model.Operation;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class OperationMapper extends GenericMapper<Operation, OperationDTO> {
	public OperationMapper() {
		super(Operation.class, OperationDTO.class);
	}
}