package org.isf.patvac.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.isf.patient.dto.PatientDTO;

import java.util.Date;

@ApiModel(description = "Class representing a patient vaccine")
public class PatientVaccineDTO {

    @ApiModelProperty(notes = "Code", example="", position = 1)
    private int code;

    @ApiModelProperty(notes = "Progr", example="", position = 2)
    private int progr;

    @ApiModelProperty(notes = "Date", example = "1979-05-01", position = 3)
    private Date vaccineDate;

    @ApiModelProperty(notes = "Patient", position = 4)
    private PatientDTO patient;

    // TODO
    // private VaccineDTO vaccine;

    private int lock;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getProgr() {
        return progr;
    }

    public void setProgr(int progr) {
        this.progr = progr;
    }

    public Date getVaccineDate() {
        return vaccineDate;
    }

    public void setVaccineDate(Date vaccineDate) {
        this.vaccineDate = vaccineDate;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }
}
