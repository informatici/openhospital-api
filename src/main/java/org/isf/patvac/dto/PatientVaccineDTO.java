package org.isf.patvac.dto;

import org.isf.patient.dto.PatientDTO;

import java.util.Date;

public class PatientVaccineDTO {

    private int code;

    private int progr;

    private Date vaccineDate;

    private PatientDTO patient;

//    private VaccineDTO vaccine;

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
