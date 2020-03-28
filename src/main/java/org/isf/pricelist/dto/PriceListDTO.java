package org.isf.pricelist.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing price list")
public class PriceListDTO {

    @ApiModelProperty(notes = "Id", example = "", position = 1)
    private int id;

    @ApiModelProperty(notes = "Code", example = "", position = 2)
    private String code;

    @ApiModelProperty(notes = "Notes", example = "", position = 3)
    private String name;

    @ApiModelProperty(notes = "Description", example = "", position = 4)
    private String description;

    @ApiModelProperty(notes = "Currency", example = "", position = 5)
    private String currency;

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
}
