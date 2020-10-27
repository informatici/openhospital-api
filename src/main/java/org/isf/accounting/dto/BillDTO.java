package org.isf.accounting.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.patient.dto.PatientDTO;
import org.isf.priceslist.dto.PriceListDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a bill")
public class BillDTO {
	
	private Integer id;
	
	private PatientDTO patient; 
	
	private Integer listId;

	@NotNull
	@ApiModelProperty(notes = "Date of bill creation", example="2020-03-19T14:58:00.000Z", position = 1)
	private Date date;
	
	@NotNull
	@ApiModelProperty(notes = "Date of bill updated", example="2020-03-19T14:58:00.000Z", position = 2)
	private Date update;
	
	@NotNull
	@ApiModelProperty(notes = "boolean wich tells if a price list is applied", example="true", position = 3)
	private boolean isList;
	
	@NotNull
	@ApiModelProperty(notes = "Price list name", example="Basic", position = 4)
	private String listName;
	
	@NotNull
	@ApiModelProperty(notes = "Is bill belongs to a patient?", example="true", position = 5)
	private boolean isPatient;
	
	@NotNull
	@ApiModelProperty(notes = "patient name", example="Mario Rossi", position = 6)
	private String patName;
	
	@NotNull
	@ApiModelProperty(notes = "Bill status", example="O", position = 7)
	private String status;

	@NotNull
	@ApiModelProperty(notes = "Bill Amount", example="1000", position = 8)
	private Double amount;
	
	@NotNull
	@ApiModelProperty(notes = "Bill balance", example="1500", position = 9)
	private Double balance;
	
	@NotNull
	@ApiModelProperty(notes = "user name who create the bill", example="admin", position = 10)
	private String user;
	
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

	public Date getUpdate() {
		return update;
	}

	public void setUpdate(Date update) {
		this.update = update;
	}

	public boolean isList() {
		return isList;
	}

	public void setList(boolean isList) {
		this.isList = isList;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public boolean isPatient() {
		return isPatient;
	}

	public void setPatient(boolean isPatient) {
		this.isPatient = isPatient;
	}

	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public PatientDTO getPatientDTO() {
		return patient;
	}

	public void setPatientDTO(PatientDTO patient) {
		this.patient = patient;
	}
	
}
