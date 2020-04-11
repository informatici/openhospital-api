package org.isf.pregtreattype.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(description = "Class representing pregnant treatment type")
public class PregnantTreatmentTypeDTO {

    @ApiModelProperty(notes = "Code", example = "", position = 1)
    private String code;

    @NotNull
    @ApiModelProperty(notes = "Description", example = "", position = 2)
    private String description;

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

}
