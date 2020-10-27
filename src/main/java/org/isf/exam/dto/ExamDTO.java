package org.isf.exam.dto;

import io.swagger.annotations.ApiModelProperty;
import org.isf.exatype.dto.ExamTypeDTO;

public class ExamDTO {

    @ApiModelProperty(notes = "Exam Code", example = "99.99", position = 1)
    private String code;

    @ApiModelProperty(notes = "Exam Description", example = "99.99 HB", position = 2)
    private String description;

    @ApiModelProperty(notes = "Exam Procedure", example = "1", position = 3)
    private Integer procedure;

    @ApiModelProperty(notes = "Exam Default Result", example = ">=12 (NORMAL)", position = 4)
    private String defaultResult;

    private ExamTypeDTO examtype;

    @ApiModelProperty(notes = "Exam Db Version to increment", example = "0", position = 5)
    private Integer lock;

    public ExamDTO() {
    }

    public ExamDTO(String code, String description, Integer procedure, String defaultResult, ExamTypeDTO examtype, Integer lock) {
        this.code = code;
        this.description = description;
        this.procedure = procedure;
        this.defaultResult = defaultResult;
        this.examtype = examtype;
        this.lock = lock;
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

    public Integer getProcedure() {
        return procedure;
    }

    public void setProcedure(Integer procedure) {
        this.procedure = procedure;
    }

    public String getDefaultResult() {
        return defaultResult;
    }

    public void setDefaultResult(String defaultResult) {
        this.defaultResult = defaultResult;
    }

    public ExamTypeDTO getExamtype() {
        return examtype;
    }

    public void setExamtype(ExamTypeDTO examtype) {
        this.examtype = examtype;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }

    @Override
    public String toString() {
        return "ExamDTO{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", procedure=" + procedure +
                ", defaultResult='" + defaultResult + '\'' +
                ", examtype=" + examtype +
                ", lock=" + lock +
                '}';
    }
}