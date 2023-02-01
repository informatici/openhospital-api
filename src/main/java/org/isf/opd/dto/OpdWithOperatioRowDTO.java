package org.isf.opd.dto;

import java.util.List;

import org.isf.operation.dto.OperationRowDTO;

import com.drew.lang.annotations.NotNull;

public class OpdWithOperatioRowDTO {
	
	@NotNull
	private OpdDTO opdDTO;
	
	private List<OperationRowDTO> operationRows;
	
	public OpdDTO getOpdDTO() {
		return opdDTO;
	}
	public void setOpdDTO(OpdDTO opdDTO) {
		this.opdDTO = opdDTO;
	}
	public List<OperationRowDTO> getOperationRows() {
		return operationRows;
	}
	public void setOperationRows(List<OperationRowDTO> operationRows) {
		this.operationRows = operationRows;
	}
	
	

}
