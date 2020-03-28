package org.isf.vaccine.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.isf.vactype.dto.VaccineTypeDTO;
import org.isf.vactype.model.VaccineType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@ApiModel(description = "Class representing a vaccine")
public class VaccineDTO {

    @NotNull
    @ApiModelProperty(notes = "Code of the vaccine", example="1", position = 1)
    private String code;

    @NotNull
    @ApiModelProperty(notes = "Description of the vaccine", example="BCG", position = 2)
    private String description;

    @NotNull
    @ApiModelProperty(notes = "Type of the vaccine", position = 3)
    private VaccineTypeDTO vaccineType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VaccineDTO code(String code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VaccineDTO description(String description) {
        this.description = description;
        return this;
    }

    public VaccineTypeDTO getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(VaccineTypeDTO vaccineType) {
        this.vaccineType = vaccineType;
    }

    public VaccineType getVaccineTypeFromDTO(VaccineTypeDTO vaccineTypeDTO){
        return getObjectMapper().map(vaccineTypeDTO, VaccineType.class);
    }

    public VaccineDTO vaccineType(VaccineTypeDTO vaccineType) {
        this.vaccineType = vaccineType;
        return this;
    }

    @Override
    public String toString() {
        return "VaccineDTO{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", vaccineType=" + vaccineType +
                '}';
    }
}
