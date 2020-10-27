package org.isf.medical.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.isf.medtype.dto.MedicalTypeDTO;

import io.swagger.annotations.ApiModelProperty;

public class MedicalDTO {
	@ApiModelProperty(notes="The id of the medical", example = "1", position = 1)
	private Integer code;
	
	@NotEmpty(message="The product code is required")
	@ApiModelProperty(notes="The product code", example = "PARA", position = 2)
	private String prod_code;
	
	@NotNull(message="The medical type is required")
	@ApiModelProperty(notes="The medical type", position = 3)
	private MedicalTypeDTO type;
	
	@NotEmpty(message="The description of the medical is required")
	@ApiModelProperty(notes="The description of the medical", example = "Paracétamol", position = 4)
	private String description;
	
	@NotNull(message="Initial quantity is required")
	@ApiModelProperty(notes="The initial quantity of the medical", example = "21", position = 5)
	private double initialqty;
	
	@NotNull(message="The number of pieces per packet is required")
	@ApiModelProperty(notes="The number of pieces per packet", example = "100", position = 6)
	private Integer pcsperpck;
	
	@NotNull(message="The input quantity is required")
	@ApiModelProperty(notes="The input quantity of the medical", example = "340", position = 7)
	private double inqty;
	
	@NotNull(message="The out quantity is required")
	@ApiModelProperty(notes="The out quantity of the medical", example = "8", position = 8)
	private double outqty;
	
	@NotNull(message="The min quantity is required")
	@ApiModelProperty(notes="The min quantity of the medical", example = "15", position = 9)
	private double minqty;
	
	public MedicalDTO() { }
	
	/**
	 * Constructor
	 */
	public MedicalDTO(Integer code, MedicalTypeDTO type, String prod_code, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty) {
		super();
		this.code = code;
		this.type = type;
		this.prod_code = prod_code;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getProd_code() {
		return prod_code;
	}

	public void setProd_code(String prod_code) {
		this.prod_code = prod_code;
	}

	public MedicalTypeDTO getType() {
		return type;
	}

	public void setType(MedicalTypeDTO type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getInitialqty() {
		return initialqty;
	}

	public void setInitialqty(double initialqty) {
		this.initialqty = initialqty;
	}

	public Integer getPcsperpck() {
		return pcsperpck;
	}

	public void setPcsperpck(Integer pcsperpck) {
		this.pcsperpck = pcsperpck;
	}

	public double getInqty() {
		return inqty;
	}

	public void setInqty(double inqty) {
		this.inqty = inqty;
	}

	public double getOutqty() {
		return outqty;
	}

	public void setOutqty(double outqty) {
		this.outqty = outqty;
	}

	public double getMinqty() {
		return minqty;
	}

	public void setMinqty(double minqty) {
		this.minqty = minqty;
	}
	
}
