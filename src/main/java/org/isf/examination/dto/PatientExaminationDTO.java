package org.isf.examination.dto;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Date;

public class PatientExaminationDTO {

    @ApiModelProperty(notes = "Patient Examination Id", example = "1", position = 1)
    private int pex_ID;

    @ApiModelProperty(notes = "Date of Patient Examination", example = "2020-03-19T14:58:00.000Z", position = 2)
    private Date pex_date;

    @ApiModelProperty(notes = "Patient Examination Code", position = 3)
    private Integer patientCode;

    @ApiModelProperty(notes = "Patient Height", position = 4)
    private Integer pex_height;

    @ApiModelProperty(notes = "Patient Weight", position = 5)
    private Double pex_weight;

    private Integer pex_pa_min;

    private Integer pex_pa_max;

    private Integer pex_fc;

    @ApiModelProperty(notes = "Patient Temperature", position = 10)
    private Double pex_temp;

    @ApiModelProperty(notes = "Patient Saturation", position = 11)
    private Double pex_sat;

    @ApiModelProperty(notes = "Examination Note", position = 12)
    private String pex_note;

    public int getPex_ID() {
        return pex_ID;
    }

    public void setPex_ID(int pex_ID) {
        this.pex_ID = pex_ID;
    }

    public Date getPex_date() {
        return pex_date;
    }

    public void setPex_date(Date pex_date) {
        this.pex_date = pex_date;
    }

    public Integer getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(Integer patientCode) {
        this.patientCode = patientCode;
    }

    public Integer getPex_height() {
        return pex_height;
    }

    public void setPex_height(Integer pex_height) {
        this.pex_height = pex_height;
    }

    public Double getPex_weight() {
        return pex_weight;
    }

    public void setPex_weight(Double pex_weight) {
        this.pex_weight = pex_weight;
    }

    public Integer getPex_pa_min() {
        return pex_pa_min;
    }

    public void setPex_pa_min(Integer pex_pa_min) {
        this.pex_pa_min = pex_pa_min;
    }

    public Integer getPex_pa_max() {
        return pex_pa_max;
    }

    public void setPex_pa_max(Integer pex_pa_max) {
        this.pex_pa_max = pex_pa_max;
    }

    public Integer getPex_fc() {
        return pex_fc;
    }

    public void setPex_fc(Integer pex_fc) {
        this.pex_fc = pex_fc;
    }

    public Double getPex_temp() {
        return pex_temp;
    }

    public void setPex_temp(Double pex_temp) {
        this.pex_temp = pex_temp;
    }

    public Double getPex_sat() {
        return pex_sat;
    }

    public void setPex_sat(Double pex_sat) {
        this.pex_sat = pex_sat;
    }

    public String getPex_note() {
        return pex_note;
    }

    public void setPex_note(String pex_note) {
        this.pex_note = pex_note;
    }
}
