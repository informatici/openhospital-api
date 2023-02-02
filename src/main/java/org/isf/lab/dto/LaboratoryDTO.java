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
package org.isf.lab.dto;

import java.time.LocalDateTime;

import org.isf.exam.dto.ExamDTO;

import com.drew.lang.annotations.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class LaboratoryDTO {

    @ApiModelProperty(notes = "Laboratory Code", position = 1)
    private Integer code;

    @ApiModelProperty(notes = "Laboratory Material", position = 2)
    private String material;

    @ApiModelProperty(notes = "Laboratory Exam", position = 3)
    private ExamDTO exam;

    @ApiModelProperty(notes = "Laboratory Registration Date", position = 4)
    private LocalDateTime registrationDate;

    @NotNull
    @ApiModelProperty(notes = "Laboratory Exam Date", position = 5)
    private LocalDateTime examDate;
    
    @ApiModelProperty(notes = "Laboratory Result", position = 6)
    private String result;
    
    @ApiModelProperty(notes = "lock", example = "0")
    private int lock;

    @ApiModelProperty(notes = "Laboratory Note", position = 7)
    private String note;

    @ApiModelProperty(notes = "Laboratory Patient Code", position = 8)
    private Integer patientCode;

    @ApiModelProperty(notes = "Laboratory Patient Name", position = 9)
    private String patName;

    @ApiModelProperty(notes = "Laboratory Patient InOut", example = "0", position = 10)
    private String inOutPatient;

    @ApiModelProperty(notes = "Laboratory Patient Age", position = 11)
    private Integer age;

    @ApiModelProperty(notes = "Laboratory Patient Sex", example = "M", position = 12)
    private String sex;

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMaterial() {
        return this.material;
    }

    public ExamDTO getExam() {
        return this.exam;
    }

    public LocalDateTime getRegistrationDate() {
        return this.registrationDate;
    }

    public LocalDateTime getDate() {
        return this.examDate;
    }

    public String getResult() {
        return this.result;
    }

    public String getNote() {
        return this.note;
    }

    public Integer getPatientCode() {
        return this.patientCode;
    }

    public String getPatName() {
        return this.patName;
    }

    public String getInOutPatient() {
        return this.inOutPatient;
    }

    public Integer getAge() {
        return this.age;
    }

    public String getSex() {
        return this.sex;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setExam(ExamDTO exam) {
        this.exam = exam;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPatientCode(Integer patientCode) {
        this.patientCode = patientCode;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public void setInOutPatient(String inOutPatient) {
        this.inOutPatient = inOutPatient;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
