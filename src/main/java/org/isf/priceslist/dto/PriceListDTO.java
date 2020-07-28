package org.isf.priceslist.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a price list")
public class PriceListDTO {
	
	private int id;

	@ApiModelProperty(notes = "the price list code", example="LISTE1", position = 1)
	private String code;
	
	@ApiModelProperty(notes = "the name of list", example="default price list", position = 2)
    private String name;
	
	@ApiModelProperty(notes = "the price list description", example="default price list", position = 3)
	private String description;

	@ApiModelProperty(notes = "the currency", example="FCFA", position = 4)
	private String currency;
	
	private int hashCode = 0;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
