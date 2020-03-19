package org.isf.accounting.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a billItem")
public class BillItemsDTO {

	private Integer id;
	
	@ApiModelProperty(notes = "Bill id", example="", position = 1)
	private Integer billId;

	public String getPriceId() {
		return priceId;
	}

	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setBillId(Integer billId) {
		this.billId = billId;
	}

	@ApiModelProperty(notes = "check if it is a price", example="true", position = 2)
	private boolean isPrice;

	@NotNull
	@ApiModelProperty(notes = "The price Id", example="104", position = 3)
	private String priceId;
	
	@NotNull
	@ApiModelProperty(notes = "item description", example="Acetone 99 % 1ltr", position = 4)
	private String itemDescription;

	@NotNull
	@ApiModelProperty(notes = "item amount", example="1000", position = 5)
	private double itemAmount;
	
	@NotNull
	@ApiModelProperty(notes = "item quantity", example="1", position = 6)
	private int itemQuantity;
	
	@NotNull
	@ApiModelProperty(notes = "item display code", example="Acetone", position = 7)
	private String itemDisplayCode;
	
	@NotNull
	@ApiModelProperty(notes = "item id", example="3", position = 8)
	private String itemId;
	
	private volatile int hashCode = 0;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public boolean isPrice() {
		return isPrice;
	}

	public void setPrice(boolean isPrice) {
		this.isPrice = isPrice;
	}
	
	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public double getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(double itemAmount) {
		this.itemAmount = itemAmount;
	}

	public int getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public String getItemDisplayCode() {
		return itemDisplayCode;
	}

	public void setItemDisplayCode(String itemDisplayCode) {
		this.itemDisplayCode = itemDisplayCode;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}	

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}
	
}
