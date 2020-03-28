package org.isf.pricelist.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.isf.priceslist.model.PriceList;

@ApiModel(description = "Class representing price")
public class PriceDTO {

    @ApiModelProperty(notes = "Id", example = "", position = 1)
    private int id;

    @ApiModelProperty(notes = "PriceList", example = "", position = 2)
    private PriceList list;

    @ApiModelProperty(notes = "Group", example = "", position = 3)
    private String group;

    @ApiModelProperty(notes = "Item", example = "", position = 4)
    private String item;

    @ApiModelProperty(notes = "Description", example = "", position = 5)
    private String description;

    @ApiModelProperty(notes = "Price", example = "", position = 6)
    private Double price;

    @ApiModelProperty(notes = "Editable", example = "", position = 7)
    private boolean editable;

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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }


}
