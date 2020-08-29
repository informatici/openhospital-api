package org.isf.examination.dto;

import io.swagger.annotations.ApiModelProperty;
import org.isf.patient.dto.PatientDTO;

import java.sql.Date;

public class PatientExaminationDTO {

    @ApiModelProperty(notes = "Patient Examination Id", example = "1", position = 1)
    private int pex_ID;

    @ApiModelProperty(notes = "Date of Patient Examination", example = "2020-03-19T14:58:00.000Z", position = 2)
    private Date pex_date;

    @ApiModelProperty(notes = "Patient Examination", position = 3)
    private PatientDTO patient;

    @ApiModelProperty(notes = "Patient Height", position = 4)
    private Integer pex_height;

    @ApiModelProperty(notes = "Patient Weight", position = 5)
    private Double pex_weight;

    private Integer pex_ap_min;

    private Integer pex_ap_max;

    private Integer pex_hr;

    private Double pex_temp;

    private Double pex_sat;

    private Integer pex_rr;

    private String pex_auscultation;

    private Integer pex_hgt;

    private Integer pex_diuresis;

    private String pex_diuresis_desc;

    private String pex_bowel_desc;

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

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
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

    public Integer getPex_ap_min() {
        return pex_ap_min;
    }

    public void setPex_ap_min(Integer pex_ap_min) {
        this.pex_ap_min = pex_ap_min;
    }

    public Integer getPex_ap_max() {
        return pex_ap_max;
    }

    public void setPex_ap_max(Integer pex_ap_max) {
        this.pex_ap_max = pex_ap_max;
    }

    public Integer getPex_hr() {
        return pex_hr;
    }

    public void setPex_hr(Integer pex_hr) {
        this.pex_hr = pex_hr;
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

    public Integer getPex_rr() {
        return pex_rr;
    }

    public void setPex_rr(Integer pex_rr) {
        this.pex_rr = pex_rr;
    }

    public String getPex_auscultation() {
        return pex_auscultation;
    }

    public void setPex_auscultation(String pex_auscultation) {
        this.pex_auscultation = pex_auscultation;
    }

    public Integer getPex_hgt() {
        return pex_hgt;
    }

    public void setPex_hgt(Integer pex_hgt) {
        this.pex_hgt = pex_hgt;
    }

    public Integer getPex_diuresis() {
        return pex_diuresis;
    }

    public void setPex_diuresis(Integer pex_diuresis) {
        this.pex_diuresis = pex_diuresis;
    }

    public String getPex_diuresis_desc() {
        return pex_diuresis_desc;
    }

    public void setPex_diuresis_desc(String pex_diuresis_desc) {
        this.pex_diuresis_desc = pex_diuresis_desc;
    }

    public String getPex_bowel_desc() {
        return pex_bowel_desc;
    }

    public void setPex_bowel_desc(String pex_bowel_desc) {
        this.pex_bowel_desc = pex_bowel_desc;
    }

    public String getPex_note() {
        return pex_note;
    }

    public void setPex_note(String pex_note) {
        this.pex_note = pex_note;
    }
}
