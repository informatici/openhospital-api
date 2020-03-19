package org.isf.visits.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.model.Patient;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.GregorianCalendar;

@ApiModel(description = "Class representing a vaccine type")
public class VisitDTO {

    private int visitID;

    @NotNull
    @ApiModelProperty(notes = "Patient related to visitor", position = 1)
    PatientDTO patient;

    @NotNull
    @ApiModelProperty(notes = "Date of the visit", position = 2)
    private GregorianCalendar date;

    @ApiModelProperty(notes = "Note of the visit", position = 3)
    private String note;

    @ApiModelProperty(notes = "Sms of the visit", position = 4)
    private boolean sms;

    @ApiModelProperty(hidden= true)
    public int getVisitID() {
        return visitID;
    }

    public void setVisitID(int visitID) {
        this.visitID = visitID;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }

    @Override
    public String toString() {
        return "VisitDTO{" +
                ", patient=" + patient.toString() +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", sms=" + sms +
                '}';
    }
}
