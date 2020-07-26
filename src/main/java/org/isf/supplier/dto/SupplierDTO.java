package org.isf.supplier.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class SupplierDTO {
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
	
}
