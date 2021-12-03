package org.isf.medicalstock.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

public class LotDTO {
	@NotNull(message="The code is required")
	@ApiModelProperty(notes="The lot's code", example = "LT001", position = 1)
	private String code;

	@NotNull(message="The preparation date is required")
	@ApiModelProperty(notes="The preparation date", example = "2020-06-24", position = 2)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date preparationDate;

	@NotNull(message="The due date is required")
	@ApiModelProperty(notes="The due date", example = "2021-06-24", position = 3)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date dueDate;

	@ApiModelProperty(notes="The lot's code", example = "750", position = 4)
	private BigDecimal cost;
	
	public LotDTO() {
		super();
	}
	
	public LotDTO(String code, Date preparationDate, Date dueDate, BigDecimal cost) {
		super();
		this.code = code;
		this.preparationDate = preparationDate;
		this.dueDate = dueDate;
		this.cost = cost;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getPreparationDate() {
		return preparationDate;
	}

	public void setPreparationDate(Date preparationDate) {
		this.preparationDate = preparationDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	
}
