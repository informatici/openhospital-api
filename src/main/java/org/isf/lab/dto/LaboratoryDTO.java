package org.isf.lab.dto;

import io.swagger.annotations.ApiModelProperty;
import org.isf.exam.dto.ExamDTO;
import org.isf.patient.dto.PatientDTO;

import java.util.Date;
import java.util.GregorianCalendar;

public class LaboratoryDTO {

    @ApiModelProperty(notes = "Laboratory Code", position = 1)
    private Integer code;

    @ApiModelProperty(notes = "Laboratory Material", position = 2)
    private String material;

    @ApiModelProperty(notes = "Laboratory Exam", position = 3)
    private ExamDTO exam;

    @ApiModelProperty(notes = "Laboratory Registration Date", position = 4)
    private Date registrationDate;

    @ApiModelProperty(notes = "Laboratory Exam Date", position = 5)
    private Date examDate;

    @ApiModelProperty(notes = "Laboratory Result", position = 6)
    private String result;

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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public ExamDTO getExam() {
        return exam;
    }

    public void setExam(ExamDTO exam) {
        this.exam = exam;
    }

    public Date getDate() {
        return registrationDate;
    }

    public void setDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(Integer patientCode) {
        this.patientCode = patientCode;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getInOutPatient() {
        return inOutPatient;
    }

    public void setInOutPatient(String inOutPatient) {
        this.inOutPatient = inOutPatient;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
