package org.isf.priceothers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a price others")
public class PricesOthersDTO {

	@ApiModelProperty(notes = "Id", example="", position = 1)
    private int id;

	@ApiModelProperty(notes = "Code", example="", position = 2)
    private String code;

	@ApiModelProperty(notes = "Description", example="", position = 3)
    private String description;

	@ApiModelProperty(notes = "OpdInclude", example="", position = 4)
    private boolean opdInclude;

	@ApiModelProperty(notes = "IpdInclude", example="", position = 5)
    private boolean ipdInclude;

	@ApiModelProperty(notes = "Daily", example="", position = 6)
    private boolean daily;

	@ApiModelProperty(notes = "Discharge", example="", position = 7)
    private boolean discharge;

	@ApiModelProperty(notes = "Undefined", example="", position = 8)
    private boolean undefined;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isOpdInclude() {
		return opdInclude;
	}

	public void setOpdInclude(boolean opdInclude) {
		this.opdInclude = opdInclude;
	}

	public boolean isIpdInclude() {
		return ipdInclude;
	}

	public void setIpdInclude(boolean ipdInclude) {
		this.ipdInclude = ipdInclude;
	}

	public boolean isDaily() {
		return daily;
	}

	public void setDaily(boolean daily) {
		this.daily = daily;
	}

	public boolean isDischarge() {
		return discharge;
	}

	public void setDischarge(boolean discharge) {
		this.discharge = discharge;
	}

	public boolean isUndefined() {
		return undefined;
	}

	public void setUndefined(boolean undefined) {
		this.undefined = undefined;
	}
}