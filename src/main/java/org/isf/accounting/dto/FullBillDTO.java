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
	
	public BillDTO getBill() {
		return bill;
	}
	public void setBill(BillDTO bill) {
		this.bill = bill;
	}
	public List<BillItemsDTO> getBillItems() {
		return billItems;
	}
	public void setBillItems(List<BillItemsDTO> billItems) {
		this.billItems = billItems;
	}
	public List<BillPaymentsDTO> getBillPayments() {
		return billPayments;
	}
	public void setBillPayments(List<BillPaymentsDTO> billPayments) {
		this.billPayments = billPayments;
	}
}
