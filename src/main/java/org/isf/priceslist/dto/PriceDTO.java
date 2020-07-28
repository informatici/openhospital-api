package org.isf.priceslist.dto;

import javax.validation.constraints.NotNull;

import org.isf.priceslist.model.PriceList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a prices")
public class PriceDTO
{

	private int id;

	@NotNull
	@ApiModelProperty(notes = "the price list", position = 1)
    private PriceList list;

	@NotNull
	@ApiModelProperty(notes = "the groupe", position = 2)
    private String group;

	@NotNull
	@ApiModelProperty(notes = "the item name", position = 3)
    private String item;

	@NotNull
	@ApiModelProperty(notes = "the description", position = 4)
    private String description;

	@NotNull
	@ApiModelProperty(notes = "price", example="1500", position = 4)
    private Double price; 
	
    private boolean editable;
	
	private int hashCode = 0;
    

	public int getId() {
		return id;
	}
	
    public void setId(int id) {
		this.id = id;
	}
	
    public PriceList getList() {
		return list;
	}
	
    public void setList(PriceList list) {
		this.list = list;
	}
	
    public String getGroup() {
		return group;
	}
	
    public void setGroup(String group) {
		this.group = group;
	}
	
    public String getItem() {
		return item;
	}
	
    public void setItem(String item) {
		this.item = item;
	}
	
    public String getDesc() {
		return description;
	}

	public void setDesc(String desc) {
		this.description = desc;
	}

	public Double getPrice() {
		return price;
	}
	
    public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isPrice() {
		return item.compareTo("") != 0;
	}

	@ApiModelProperty(hidden= true)
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public String toString() {
		return description;
	}
	
	@ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	
}
