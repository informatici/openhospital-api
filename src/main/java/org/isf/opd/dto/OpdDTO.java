package org.isf.opd.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.patient.dto.PatientDTO;

import java.util.Date;
import java.util.GregorianCalendar;

@ApiModel(description = "Class representing a opd")
public class OpdDTO {

    private Integer code;

    @ApiModelProperty(notes = "Date", example = "1979-05-01", position = 1)
    private Date date;

    @ApiModelProperty(notes = "Visit Date", example = "1979-05-01", position = 2)
    private GregorianCalendar visitDate;

    @ApiModelProperty(notes = "PatientDTO", position = 3)
    private PatientDTO patient;

    @ApiModelProperty(notes = "Age", example = "40", position = 4)
    private int age;

    @ApiModelProperty(notes = "Sex", allowableValues = "F,M", example = "M", position = 5)
    private char sex;

    @ApiModelProperty(notes = "Note", example = "Bla bla", position = 6)
    private String note; //ADDED: Alex

    @ApiModelProperty(notes = "Prog year", example = "2020", position = 7)
    private int prog_year;

    @ApiModelProperty(notes = "Disease", position = 8)
    private DiseaseDTO disease;

    @ApiModelProperty(notes = "Disease2", position = 9)
    private DiseaseDTO disease2;

    @ApiModelProperty(notes = "Disease3", position = 10)
    private DiseaseDTO disease3;

    @ApiModelProperty(notes = "New patient", allowableValues = "n,R", example = "R", position = 11)
    private char newPatient;    //n=NEW R=REATTENDANCE

    @ApiModelProperty(notes = "Referral from", allowableValues = "R,null", example = "R", position = 12)
    private String referralFrom;    //R=referral from another unit; null=no referral from

    @ApiModelProperty(notes = "Referral to", allowableValues = "R,null", example = "R", position = 13)
    private String referralTo;        //R=referral to another unit; null=no referral to

    @ApiModelProperty(notes = "User ID", example = "test", position = 14)
    private String userID;

    private int lock;

    private volatile int hashCode = 0;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public GregorianCalendar getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(GregorianCalendar visitDate) {
        this.visitDate = visitDate;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getProg_year() {
        return prog_year;
    }

    public void setProg_year(int prog_year) {
        this.prog_year = prog_year;
    }

    public DiseaseDTO getDisease() {
        return disease;
    }

    public void setDisease(DiseaseDTO disease) {
        this.disease = disease;
    }

    public DiseaseDTO getDisease2() {
        return disease2;
    }

    public void setDisease2(DiseaseDTO disease2) {
        this.disease2 = disease2;
    }

    public DiseaseDTO getDisease3() {
        return disease3;
    }

    public void setDisease3(DiseaseDTO disease3) {
        this.disease3 = disease3;
    }

    public char getNewPatient() {
        return newPatient;
    }

    public void setNewPatient(char newPatient) {
        this.newPatient = newPatient;
    }

    public String getReferralFrom() {
        return referralFrom;
    }

    public void setReferralFrom(String referralFrom) {
        this.referralFrom = referralFrom;
    }

    public String getReferralTo() {
        return referralTo;
    }

    public void setReferralTo(String referralTo) {
        this.referralTo = referralTo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
}
