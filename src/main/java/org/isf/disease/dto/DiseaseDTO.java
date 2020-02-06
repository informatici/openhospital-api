package org.isf.disease.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.isf.distype.dto.DiseaseTypeDTO;

@ApiModel(description = "Class representing a disease")
public class DiseaseDTO {

    @ApiModelProperty(notes = "Code", example="", position = 1)
    private String code;

    @ApiModelProperty(notes = "Description", example="", position = 2)
    private String description;

    @ApiModelProperty(notes = "Disease type", position = 3)
    private DiseaseTypeDTO diseaseType;

    private Integer lock;

    @ApiModelProperty(notes = "Opd include", position = 4)
    private boolean opdInclude;

    @ApiModelProperty(notes = "Opd in include", position = 5)
    private boolean ipdInInclude;

    @ApiModelProperty(notes = "Opd out include", position = 6)
    private boolean ipdOutInclude;

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

    public DiseaseTypeDTO getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(DiseaseTypeDTO diseaseType) {
        this.diseaseType = diseaseType;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }

    public boolean isOpdInclude() {
        return opdInclude;
    }

    public void setOpdInclude(boolean opdInclude) {
        this.opdInclude = opdInclude;
    }

    public boolean isIpdInInclude() {
        return ipdInInclude;
    }

    public void setIpdInInclude(boolean ipdInInclude) {
        this.ipdInInclude = ipdInInclude;
    }

    public boolean isIpdOutInclude() {
        return ipdOutInclude;
    }

    public void setIpdOutInclude(boolean ipdOutInclude) {
        this.ipdOutInclude = ipdOutInclude;
    }
}
