package org.isf.accounting.dto;


import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class FullBillDTO {
	
	@NotNull
	@ApiModelProperty(notes = "bill element", position = 1)
	private BillDTO bill;
	
	@NotNull
	@ApiModelProperty(notes = "list of bill items elements", position = 2)
	private List<BillItemsDTO> billItems;
	
	@ApiModelProperty(notes = "list of bill payments elements", position = 3)
	private List<BillPaymentsDTO> billPayments;
	
	public BillDTO getBillDTO() {
		return bill;
	}
	public void setBillDTO(BillDTO billDTO) {
		this.bill = billDTO;
	}
	public List<BillItemsDTO> getBillItemsDTO() {
		return billItems;
	}
	public void setBillItemsDTO(List<BillItemsDTO> billItemsDTO) {
		this.billItems = billItemsDTO;
	}
	public List<BillPaymentsDTO> getBillPaymentsDTO() {
		return billPayments;
	}
	public void setBillPaymentsDTO(List<BillPaymentsDTO> billPaymentsDTO) {
		this.billPayments = billPaymentsDTO;
	}
}
