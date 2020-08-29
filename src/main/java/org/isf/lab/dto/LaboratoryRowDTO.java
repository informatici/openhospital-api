package org.isf.lab.dto;

import io.swagger.annotations.ApiModelProperty;

public class LaboratoryRowDTO {

    private Integer code;
    private LaboratoryDTO laboratory;
    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public LaboratoryDTO getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(LaboratoryDTO laboratory) {
        this.laboratory = laboratory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
