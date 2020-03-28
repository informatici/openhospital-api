package org.isf.priceothers.dto;

public class PricesOthersDTO {
    private int id;

    private String code;

    private String description;

    private boolean opdInclude;

    private boolean ipdInclude;

    private boolean daily;

    private boolean discharge;

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