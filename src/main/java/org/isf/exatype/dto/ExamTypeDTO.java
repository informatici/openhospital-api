package org.isf.exatype.dto;

import io.swagger.annotations.ApiModelProperty;

public class ExamTypeDTO {

    @ApiModelProperty(notes = "Exam Type Code", example = "HB", position = 1)
    private String code;

    @ApiModelProperty(notes = "Exam Type Description", example = "1.Haematology", position = 2)
    private String description;

    public ExamTypeDTO() {
    }

    public ExamTypeDTO(String code, String description) {
        this.code = code;
        this.description = description;
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

    @Override
    public String toString() {
        return "ExamTypeDTO{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}