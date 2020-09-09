package org.isf.lab.dto;

public class LaboratoryRowDTO {

    private Integer code;
    private LaboratoryDTO laboratory;
    private String description;

    public LaboratoryRowDTO() {
    }

    public LaboratoryRowDTO(String description, LaboratoryDTO labDTO) {
        this.description = description;
        this.laboratory = labDTO;
    }

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
