package org.isf.lab.dto;

import java.util.List;

public class LabWithRowsDTO {

    private LaboratoryDTO laboratoryDTO;

    private List<String> laboratoryRowList;

    public LaboratoryDTO getLaboratoryDTO() {
        return laboratoryDTO;
    }

    public void setLaboratoryDTO(LaboratoryDTO laboratoryDTO) {
        this.laboratoryDTO = laboratoryDTO;
    }

    public List<String> getLaboratoryRowList() {
        return laboratoryRowList;
    }

    public void setLaboratoryRowList(List<String> laboratoryRowList) {
        this.laboratoryRowList = laboratoryRowList;
    }
}
