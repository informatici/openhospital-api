package org.isf.medstockmovtype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class MovementTypeDTO {
	@NotNull
	@ApiModelProperty(notes="Code of the movement type", example = "D", position = 1)
	private String code;
	
	@NotNull
	@ApiModelProperty(notes="Description of the movement type", example = "Damage", position = 2)
	private String description;
	
	@NotNull
	@ApiModelProperty(notes="Type of the movement type", example = "-", position = 3)
	private String type;
	
	public MovementTypeDTO(){}
    
    /**
     * @param code
     * @param description
     * @param type
     */
    public MovementTypeDTO(String code, String description, String type) {
        this.code = code;
        this.description = description;
        this.type = type;
    }
	
	public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
