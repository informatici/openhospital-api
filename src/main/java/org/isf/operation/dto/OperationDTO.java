package org.isf.operation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.isf.operationtype.dto.OperationTypeDTO;

@ApiModel(description = "Class representing an operation")
public class OperationDTO {

    @ApiModelProperty(notes = "Code", example = "", position = 1)
    private String code;

    @ApiModelProperty(notes = "Description", example = "", position = 2)
    private String description;

    @ApiModelProperty(notes = "Operation type", position = 3)
    private OperationTypeDTO type;

    @ApiModelProperty(notes = "Major", position = 4)
    private Integer major;

    private Integer lock;

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

    public OperationTypeDTO getOperationType() {
        return type;
    }

    public void setOperationType(OperationTypeDTO type) {
        this.type = type;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

}
