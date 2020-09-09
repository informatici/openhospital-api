package org.isf.exam.dto;

import io.swagger.annotations.ApiModelProperty;

public class ExamRowDTO {

    @ApiModelProperty(notes = "Exam Row Code", example = "999", position = 1)
    private int code;

    @ApiModelProperty(notes = "Exam Row Code", example = "NEGATIVE", position = 2)
    private String description;

    private ExamDTO exam;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExamDTO getExam() {
        return exam;
    }

    public void setExam(ExamDTO exam) {
        this.exam = exam;
    }
}
