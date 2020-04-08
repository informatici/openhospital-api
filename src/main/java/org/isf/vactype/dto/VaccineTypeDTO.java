package org.isf.vactype.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Class representing a vaccine type")
public class VaccineTypeDTO {

    @NotNull
    @ApiModelProperty(notes = "Code of the vaccine type", example="C", position = 1)
    private String code;

    @NotNull
    @ApiModelProperty(notes = "Description of the vaccine type", example="Child", position = 2)
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
