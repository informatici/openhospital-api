package org.isf.opd.dto;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.disease.dto.DiseaseDTO;
import org.isf.patient.dto.PatientDTO;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author gildas
 *
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

	@ApiModelProperty(notes = "the admited patient", position = 5)
	private PatientDTO patient;

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

	@Version
	private int lock;

	@Transient
	private volatile int hashCode = 0;

	public OpdDTO() {
	}

	/**
	 * @param aYear
	 * @param aSex
	 * @param aDate
	 * @param aAge
	 * @param aDisease
	 */
	public OpdDTO(int aProgYear, char aSex, int aAge, DiseaseDTO aDisease) {
		prog_year = aProgYear;
		sex = aSex;
		age = aAge;
		disease = aDisease;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFullName() {
		return patient.getName();
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

	public String getfirstName() {
		return patient == null ? "" : patient.getFirstName();
	}

	public String getsecondName() {
		return patient == null ? "" : patient.getSecondName();
	}

	public String getnextKin() {
		return patient == null ? "" : patient.getNextKin();
	}

	public String getcity() {
		return patient == null ? "" : patient.getCity();
	}

	public String getaddress() {
		return patient == null ? "" : patient.getAddress();
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

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + code;

			this.hashCode = c;
		}

		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof OpdDTO)) {
			return false;
		}

		OpdDTO opd = (OpdDTO) obj;
		return (code == opd.getCode());
	}
}
