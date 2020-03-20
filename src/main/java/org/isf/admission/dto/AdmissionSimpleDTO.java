package org.isf.admission.dto;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author gildas
 *
 */
public class AdmissionSimpleDTO {

	@ApiModelProperty(notes = "admission key", example = "12", position = 1)
	private int id;

	@NotNull
	@ApiModelProperty(notes = "if admitted or not", example = "0", position = 2)
	private int admitted;

	@NotNull
	@ApiModelProperty(notes = "type of admission", example = "malnutrition", position = 3)
	private String type;

	@NotNull
	@ApiModelProperty(notes = "a progr. in year for each ward", example = "1", position = 5)
	private int yProg;

	@ApiModelProperty(notes = "admission date", position = 7)
	private Date admDate;

	@ApiModelProperty(notes = "FromHealthUnit", position = 9)
	private String FHU;

	@ApiModelProperty(notes = "operation date", position = 15)
	private GregorianCalendar opDate;

	@ApiModelProperty(notes = "operation result value is 'P' or 'N' ", example = "N", position = 16)
	private String opResult;

	@ApiModelProperty(notes = "discharge date", position = 17)
	private GregorianCalendar disDate;

	@ApiModelProperty(notes = "free note", position = 19)
	private String note;

	@ApiModelProperty(notes = "transfusional unit", position = 20)
	private Float transUnit;

	@ApiModelProperty(notes = "visite date", position = 21)
	private GregorianCalendar visitDate;

	@ApiModelProperty(notes = "delivery date", position = 23)
	private GregorianCalendar deliveryDate;

	@ApiModelProperty(notes = "weight", position = 26)
	private Float weight;

	private GregorianCalendar ctrlDate1;

	private GregorianCalendar ctrlDate2;

	private GregorianCalendar abortDate;

	@ApiModelProperty(notes = "weight", position = 30)
	private String userID;

	@ApiModelProperty(notes = "lock", example = "0", position = 31)
	private int lock;

	@NotNull
	@ApiModelProperty(notes = "flag record deleted, values are 'Y' OR 'N' ", example = "N", position = 32)
	private String deleted;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdmitted() {
		return admitted;
	}

	public void setAdmitted(int admitted) {
		this.admitted = admitted;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getyProg() {
		return yProg;
	}

	public void setyProg(int yProg) {
		this.yProg = yProg;
	}

	public Date getAdmDate() {
		return admDate;
	}

	public void setAdmDate(Date admDate) {
		this.admDate = admDate;
	}

	public String getFHU() {
		return FHU;
	}

	public void setFHU(String fHU) {
		FHU = fHU;
	}

	public GregorianCalendar getOpDate() {
		return opDate;
	}

	public void setOpDate(GregorianCalendar opDate) {
		this.opDate = opDate;
	}

	public String getOpResult() {
		return opResult;
	}

	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}

	public GregorianCalendar getDisDate() {
		return disDate;
	}

	public void setDisDate(GregorianCalendar disDate) {
		this.disDate = disDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Float getTransUnit() {
		return transUnit;
	}

	public void setTransUnit(Float transUnit) {
		this.transUnit = transUnit;
	}

	public GregorianCalendar getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(GregorianCalendar visitDate) {
		this.visitDate = visitDate;
	}

	public GregorianCalendar getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(GregorianCalendar deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public GregorianCalendar getCtrlDate1() {
		return ctrlDate1;
	}

	public void setCtrlDate1(GregorianCalendar ctrlDate1) {
		this.ctrlDate1 = ctrlDate1;
	}

	public GregorianCalendar getCtrlDate2() {
		return ctrlDate2;
	}

	public void setCtrlDate2(GregorianCalendar ctrlDate2) {
		this.ctrlDate2 = ctrlDate2;
	}

	public GregorianCalendar getAbortDate() {
		return abortDate;
	}

	public void setAbortDate(GregorianCalendar abortDate) {
		this.abortDate = abortDate;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

}
