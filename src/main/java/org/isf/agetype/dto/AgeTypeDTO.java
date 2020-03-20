package org.isf.agetype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing an age type which is typically a range")
public class AgeTypeDTO {
	// properties
	@ApiModelProperty(notes = "Age type code")
	private String code;

	@NotNull
	@ApiModelProperty(notes = "Age type description")
    private String description;

	@NotNull	
	@ApiModelProperty(notes = "The minimum value of the range", example="0")
    private int from;

	@NotNull
	@ApiModelProperty(notes = "The maximum value of the range", example="0")
    private int to;

	// getters and setters
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

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}
	
	
}
