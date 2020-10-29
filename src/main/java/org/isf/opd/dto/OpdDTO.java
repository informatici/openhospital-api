/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.disease.dto.DiseaseDTO;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author gildas
 */
public class OpdDTO {

    private int code;

    @ApiModelProperty(notes = "the date of the admission", position = 2)
    private Date date;

    @NotNull
    @ApiModelProperty(notes = "the visite date", position = 3)
    private Date visitDate;

    @ApiModelProperty(notes = "the next visite date", position = 4)
    private Date nextVisitDate;

    @ApiModelProperty(notes = "the admited patient code", position = 5)
    private Integer patientCode;

    @NotNull
    @ApiModelProperty(notes = "the patient age", example = "18", position = 6)
    private int age;

    @NotNull
    @ApiModelProperty(notes = "the patient sex", example = "M", position = 7)
    private char sex;

    @NotNull
    @ApiModelProperty(notes = "the admission note", example = "", position = 8)
    private String note; // ADDED: Alex

    @NotNull
    @ApiModelProperty(notes = "a progr. in year for each ward", example = "18", position = 9)
    private int prog_year;

    @ApiModelProperty(notes = "disease", position = 10)
    private DiseaseDTO disease;

    @ApiModelProperty(notes = "disease 2", position = 11)
    private DiseaseDTO disease2;

    @ApiModelProperty(notes = "disease 3", position = 12)
    private DiseaseDTO disease3;

    @NotNull
    @ApiModelProperty(notes = "new(N) or reattendance(R) patient", example = "N", position = 13)
    private char newPatient; // n=NEW R=REATTENDANCE

    @ApiModelProperty(notes = "referral from another unit", example = "R", position = 14)
    private String referralFrom; // R=referral from another unit; null=no referral from

    @ApiModelProperty(notes = "referral to another unit", example = "R", position = 15)
    private String referralTo; // R=referral to another unit; null=no referral to

    @ApiModelProperty(notes = "user id", position = 16)
    private String userID;

    @ApiModelProperty(notes = "opd lock column", position = 16)
    private int lock;

    private int hashCode = 0;


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(Integer patientCode) {
        this.patientCode = patientCode;
    }

    public char getNewPatient() {
        return newPatient;
    }

    public void setNewPatient(char newPatient) {
        this.newPatient = newPatient;
    }

    public String getReferralTo() {
        return referralTo;
    }

    public void setReferralTo(String referralTo) {
        this.referralTo = referralTo;
    }

    public String getReferralFrom() {
        return referralFrom;
    }

    public void setReferralFrom(String referralFrom) {
        this.referralFrom = referralFrom;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DiseaseDTO getDisease() {
        return disease;
    }

    public DiseaseDTO getDisease2() {
        return disease2;
    }

    public DiseaseDTO getDisease3() {
        return disease3;
    }

    public void setDisease(DiseaseDTO disease) {
        this.disease = disease;
    }

    public void setDisease2(DiseaseDTO disease) {
        this.disease2 = disease;
    }

    public void setDisease3(DiseaseDTO disease) {
        this.disease3 = disease;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    @ApiModelProperty(hidden = true)
    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visDate) {
        this.visitDate = visDate;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public int getProgYear() {
        return prog_year;
    }

    public void setProgYear(int prog_year) {
        this.prog_year = prog_year;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getNextVisitDate() {
        return nextVisitDate;
    }

    public void setNextVisitDate(Date nextVisitDate) {
        this.nextVisitDate = nextVisitDate;
    }

}
