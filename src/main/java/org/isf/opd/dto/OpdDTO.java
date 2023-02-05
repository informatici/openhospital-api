/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.opd.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.isf.disease.dto.DiseaseDTO;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author gildas
 */
public class OpdDTO {
    
    @ApiModelProperty(notes = "the code of the opd", example="3", position = 1)
    private int code;

    @ApiModelProperty(notes = "the date of the admission", position = 2)
    private LocalDateTime date;

    @NotNull
    @ApiModelProperty(notes = "the visit date", position = 3)
    @Deprecated
    private LocalDateTime visitDate;

    @ApiModelProperty(notes = "the next visit date", position = 4)
    private LocalDateTime nextVisitDate;

    @ApiModelProperty(notes = "the admitted patient code", position = 5)
    private Integer patientCode;

    @NotNull
    @ApiModelProperty(notes = "the patient age", example = "18", position = 6)
    private int age;

    @NotNull
    @ApiModelProperty(notes = "the patient sex", example = "M", position = 7)
    private char sex;
    
    //@NotNull
    @ApiModelProperty(notes = "the patient sex", example = "M", position = 8)
    private String patientName;
    
    @ApiModelProperty(notes = "Age type", example="null", position = 9)
    private String ageType; // ADDED: Arnaud

    @NotNull
    @ApiModelProperty(notes = "the admission note", example = "this is out patient", position = 10)
    private String note; // ADDED: Alex

    //@NotNull
    @ApiModelProperty(notes = "a progr. in year for each ward", example = "18", position = 11)
    private int prog_year;

    @ApiModelProperty(notes = "disease", position = 12)
    private DiseaseDTO disease;

    @ApiModelProperty(notes = "disease 2", position = 13)
    private DiseaseDTO disease2;

    @ApiModelProperty(notes = "disease 3", position = 14)
    private DiseaseDTO disease3;

    @NotNull
    @ApiModelProperty(notes = "new(N) or reattendance(R) patient", example = "N", position = 15)
    private char newPatient; // n=NEW R=REATTENDANCE

    @ApiModelProperty(notes = "referral from another unit", example = "R", position = 16)
    private String referralFrom; // R=referral from another unit; null=no referral from

    @ApiModelProperty(notes = "referral to another unit", example = "R", position = 17)
    private String referralTo; // R=referral to another unit; null=no referral to

    @ApiModelProperty(notes = "user id", position = 18)
    private String userID;

    @ApiModelProperty(notes = "lock", example = "0")
    private int lock;
    
    private int hashCode;
    
    @ApiModelProperty(notes = "reasons for entry", position = 19)
    private String reason; // ADDED: Arnaud
    
    @ApiModelProperty(notes = "history of a medical or psychiatric patient", position = 20)
    private String anamnesis; // ADDED: Arnaud
    
    @ApiModelProperty(notes = "allergies of patient", position = 21)
    private String allergies; // ADDED: Arnaud
    
    @ApiModelProperty(notes = "Current therapies", position = 22)
    private String therapies; // ADDED: Arnaud
    
    @ApiModelProperty(notes = "prescription", position = 23)
    private String prescription; // ADDED: Arnaud
    
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getTherapies() {
        return therapies;
    }

    public void setTherapies(String therapies) {
        this.therapies = therapies;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    @ApiModelProperty(hidden=true)
    public int getLock() {
        return lock;
    }

    @ApiModelProperty(hidden=true)
    public int getHashCode() {
        return hashCode;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public int getCode() {
        return this.code;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public LocalDateTime getVisitDate() {
        return this.visitDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDateTime getNextVisitDate() {
        return this.nextVisitDate;
    }

    public Integer getPatientCode() {
        return this.patientCode;
    }

    public int getAge() {
        return this.age;
    }

    public char getSex() {
        return this.sex;
    }

    public String getNote() {
        return this.note;
    }

    public int getProg_year() {
        return this.prog_year;
    }

    public DiseaseDTO getDisease() {
        return this.disease;
    }

    public DiseaseDTO getDisease2() {
        return this.disease2;
    }

    public DiseaseDTO getDisease3() {
        return this.disease3;
    }

    public char getNewPatient() {
        return this.newPatient;
    }

    public String getReferralFrom() {
        return this.referralFrom;
    }

    public String getReferralTo() {
        return this.referralTo;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public void setNextVisitDate(LocalDateTime nextVisitDate) {
        this.nextVisitDate = nextVisitDate;
    }

    public void setPatientCode(Integer patientCode) {
        this.patientCode = patientCode;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setProg_year(int prog_year) {
        this.prog_year = prog_year;
    }

    public void setDisease(DiseaseDTO disease) {
        this.disease = disease;
    }

    public void setDisease2(DiseaseDTO disease2) {
        this.disease2 = disease2;
    }

    public void setDisease3(DiseaseDTO disease3) {
        this.disease3 = disease3;
    }

    public void setNewPatient(char newPatient) {
        this.newPatient = newPatient;
    }

    public void setReferralFrom(String referralFrom) {
        this.referralFrom = referralFrom;
    }

    public void setReferralTo(String referralTo) {
        this.referralTo = referralTo;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public String getAgeType() {
        return ageType;
    }

    public void setAgeType(String ageType) {
        this.ageType = ageType;
    }
    
    
}
