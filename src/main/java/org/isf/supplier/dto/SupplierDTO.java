package org.isf.supplier.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class SupplierDTO {
	@NotNull(message="supplier's ID is required")
	@ApiModelProperty(notes="The supplier's ID", example = "111", position = 1)
	private Integer supId;
	
	@NotNull(message="supplier's name is required")
	@ApiModelProperty(notes="The supplier's name", example = "Cogefar", position = 2)
	private String supName;
	
	@ApiModelProperty(notes="The supplier's address", example = "25 Rue Ministre, Dschang", position = 3)
	private String supAddress;
	
	@ApiModelProperty(notes="The supplier's tax code", example = "5221", position = 4)
	private String supTaxcode;
	
	@ApiModelProperty(notes="The supplier's phone", example = "+237654120145", position = 5)
	private String supPhone;
	
	@ApiModelProperty(notes="The supplier's fax number", example = "+237654120145", position = 6)
	private String supFax;
	
	@ApiModelProperty(notes="The supplier's e-mail address", example = "suplier@sample.com", position = 7)
	private String supEmail;
	
	@ApiModelProperty(notes="The supplier's notes", example = "", position = 8)
	private String supNote;

	public SupplierDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SupplierDTO(Integer supId, String supName, String supAddress, String supTaxcode, String supPhone,
			String supFax, String supEmail, String supNote) {
		super();
		this.supId = supId;
		this.supName = supName;
		this.supAddress = supAddress;
		this.supTaxcode = supTaxcode;
		this.supPhone = supPhone;
		this.supFax = supFax;
		this.supEmail = supEmail;
		this.supNote = supNote;
	}



	public Integer getSupId() {
		return supId;
	}

	public void setSupId(Integer supId) {
		this.supId = supId;
	}

	public String getSupName() {
		return supName;
	}

	public void setSupName(String supName) {
		this.supName = supName;
	}

	public String getSupAddress() {
		return supAddress;
	}

	public void setSupAddress(String supAddress) {
		this.supAddress = supAddress;
	}

	public String getSupTaxcode() {
		return supTaxcode;
	}

	public void setSupTaxcode(String supTaxcode) {
		this.supTaxcode = supTaxcode;
	}

	public String getSupPhone() {
		return supPhone;
	}

	public void setSupPhone(String supPhone) {
		this.supPhone = supPhone;
	}

	public String getSupFax() {
		return supFax;
	}

	public void setSupFax(String supFax) {
		this.supFax = supFax;
	}

	public String getSupEmail() {
		return supEmail;
	}

	public void setSupEmail(String supEmail) {
		this.supEmail = supEmail;
	}

	public String getSupNote() {
		return supNote;
	}

	public void setSupNote(String supNote) {
		this.supNote = supNote;
	}
	
}
