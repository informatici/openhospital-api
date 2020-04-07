package org.isf.accounting.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a billPayment")
public class BillPaymentsDTO {
	
	private Integer id;

	@NotNull
	@ApiModelProperty(notes = "Bill id", example="", position = 1)
	private Integer billId;

	@NotNull
	@ApiModelProperty(notes = "date of payment", example="2020-03-19T14:58:00.000Z", position = 2)
	private Date date;

	@NotNull
	@ApiModelProperty(notes = "the payment amount", example="500", position = 3)
	private double amount;

	@NotNull
	@ApiModelProperty(notes = "the current user", example="admin", position = 4)
	private String user;

	private volatile int hashCode = 0;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}
	
}
