package org.isf.opetype.mapper;

import org.isf.opetype.dto.OperationTypeDTO;
import org.isf.opetype.model.OperationType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class OperationTypeMapper extends GenericMapper<OperationType, OperationTypeDTO> {
	public OperationTypeMapper() {
		super(OperationType.class, OperationTypeDTO.class);
	}
}