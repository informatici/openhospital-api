package org.isf.agetype.dto;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author Martin Xavier
 *
 */
public class AgeTypeDTO {
	// properties
	private String code;

	@NotNull
    private String description;

	@NotNull	
    private int from;

	@NotNull
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
