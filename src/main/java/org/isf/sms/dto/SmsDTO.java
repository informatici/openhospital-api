package org.isf.sms.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

public class SmsDTO {
	@ApiModelProperty(notes = "SMS Id", example="1", position = 1)
	private Integer smsId;
	
	@NotNull
	@ApiModelProperty(notes = "SMS Date", example="2020-07-16", position = 2)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date smsDate;
	
	@NotNull
	@ApiModelProperty(notes = "SMS scheduled date", example="2020-07-28", position = 3)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date smsDateSched;
	
	@NotNull
	@ApiModelProperty(notes = "SMS target phone number", example="+237671302313", position = 4)
	private String smsNumber;
	
	@NotNull
	@ApiModelProperty(notes = "SMS content text", example="Hi Mario!", position = 5)
	private String smsText;
	
	@ApiModelProperty(notes = "SMS sent date", example="2020-07-28", position = 6)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date smsDateSent;
	
	@NotNull
	@ApiModelProperty(notes = "SMS user", example="Rosi", position = 7)
	private String smsUser;
	
	@NotNull
	@ApiModelProperty(notes = "SMS module name", example="OPD", position = 8)
	private String module;
	
	@ApiModelProperty(notes = "SMS module Id", position = 9)
	private String moduleID;

	public SmsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SmsDTO(Integer smsId, Date smsDate, Date smsDateSched, String smsNumber, String smsText, Date smsDateSent,
			String smsUser, String module, String moduleID) {
		super();
		this.smsId = smsId;
		this.smsDate = smsDate;
		this.smsDateSched = smsDateSched;
		this.smsNumber = smsNumber;
		this.smsText = smsText;
		this.smsDateSent = smsDateSent;
		this.smsUser = smsUser;
		this.module = module;
		this.moduleID = moduleID;
	}

	public Integer getSmsId() {
		return smsId;
	}

	public void setSmsId(Integer smsId) {
		this.smsId = smsId;
	}

	public Date getSmsDate() {
		return smsDate;
	}

	public void setSmsDate(Date smsDate) {
		this.smsDate = smsDate;
	}

	public Date getSmsDateSched() {
		return smsDateSched;
	}

	public void setSmsDateSched(Date smsDateSched) {
		this.smsDateSched = smsDateSched;
	}

	public String getSmsNumber() {
		return smsNumber;
	}

	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public Date getSmsDateSent() {
		return smsDateSent;
	}

	public void setSmsDateSent(Date smsDateSent) {
		this.smsDateSent = smsDateSent;
	}

	public String getSmsUser() {
		return smsUser;
	}

	public void setSmsUser(String smsUser) {
		this.smsUser = smsUser;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModuleID() {
		return moduleID;
	}

	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}
	
}
