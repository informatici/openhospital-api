package org.isf.admtype.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Not used anymore
 *
 * @author antonio
 */
@Getter
@Setter
public class AdmissionTypeDTO {
	
	@NotNull
	@ApiModelProperty(notes = "code of the admission type", example="A", position = 1)
	private String code;
	
	@NotNull
	@ApiModelProperty(notes = "description of the admission type", example="AMBULANCE", position = 2)
    private String description;
}
