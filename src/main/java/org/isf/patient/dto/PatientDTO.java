package org.isf.patient.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.eclipse.jdt.core.dom.NullLiteral;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(description = "Class representing a patient")
public class PatientDTO {

	private Integer code;

	@NotNull
	@ApiModelProperty(notes = "First name of the patient", example="Mario", position = 1)
	private String firstName;

	@NotNull
	@ApiModelProperty(notes = "Last name of the patient", example="Rossi", position = 2)
	private String secondName;

	private String name;

	@ApiModelProperty(notes = "Birth date", example="1979-05-01", position = 3)
	private Date birthDate;

	@NotNull
	@ApiModelProperty(notes = "Age", example="40", position = 4)
	private int age;

	@ApiModelProperty(notes = "Age type", example="null", position = 5)
	private String agetype;

	@NotNull
	@ApiModelProperty(notes = "Sex", allowableValues = "F,M", example = "M", position = 6)
	private char sex;

	@ApiModelProperty(notes = "Address", example="Via Roma, 12", position = 7)
	private String address;

	@NotNull
	@ApiModelProperty(notes = "City", example="Verona", position = 8)
	private String city;

	private String nextKin;

	@ApiModelProperty(notes = "Telephone", example="+393456789012", position = 9)
	private String telephone;

	@ApiModelProperty(notes = "Note", example="Test insert new patient", position = 10)
	private String note;

	@NotNull
	@ApiModelProperty(notes = "Mother's name", example="Roberta", position = 11)
	private String mother_name;

	@ApiModelProperty(notes = "Mother's status (D=dead, A=alive)", allowableValues = "D,A", example="A", position = 12)
	private char mother = ' ';

	@NotNull
	@ApiModelProperty(notes = "Father's name", example="Giuseppe", position = 13)
	private String father_name;

	@ApiModelProperty(notes = "Father's status (D=dead, A=alive)", allowableValues = "D,A", example="D", position = 14)
	private char father = ' ';

	@NotNull
	@ApiModelProperty(notes = "Blood type (0-/+, A-/+ , B-/+, AB-/+)", allowableValues = "0-,0+,A-,A+,B-,B+,AB-,AB+", example="A+", position = 15)
	private String bloodType;

	@ApiModelProperty(notes = "hasInsurance (Y=Yes, N=no)", allowableValues = "Y,N", example="N", position = 16)
	private char hasInsurance = ' ';

	@ApiModelProperty(notes = "Parent together (Y=Yes, N=no)", allowableValues = "Y,N", example="N", position = 17)
	private char parentTogether = ' ';

	@ApiModelProperty(notes = "Tax code", example="RSSMRA79E01L781N", position = 18)
	private String taxCode;

	@ApiModelProperty(notes = "Height", example="174", position = 19)
	private float height;

	@ApiModelProperty(notes = "Weight", example="73", position = 20)
	private float weight;
	
	private int lock;

	@ApiModelProperty(notes = "BlobPhoto", example="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", position = 21)
	private byte[] blobPhoto;

	private int hashCode = 0;

	@ApiModelProperty(hidden= true)
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	@ApiModelProperty(hidden= true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAgetype() {
		return agetype;
	}

	public void setAgetype(String agetype) {
		this.agetype = agetype;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@ApiModelProperty(hidden= true)
	public String getNextKin() {
		return nextKin;
	}

	public void setNextKin(String nextKin) {
		this.nextKin = nextKin;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getMother_name() {
		return mother_name;
	}

	public void setMother_name(String mother_name) {
		this.mother_name = mother_name;
	}

	public char getMother() {
		return mother;
	}

	public void setMother(char mother) {
		this.mother = mother;
	}

	public String getFather_name() {
		return father_name;
	}

	public void setFather_name(String father_name) {
		this.father_name = father_name;
	}

	public char getFather() {
		return father;
	}

	public void setFather(char father) {
		this.father = father;
	}

	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public char getHasInsurance() {
		return hasInsurance;
	}

	public void setHasInsurance(char hasInsurance) {
		this.hasInsurance = hasInsurance;
	}

	public char getParentTogether() {
		return parentTogether;
	}

	public void setParentTogether(char parentTogether) {
		this.parentTogether = parentTogether;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	@ApiModelProperty(hidden= true)
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

    public byte[] getBlobPhoto() {
        return blobPhoto;
    }

    public void setBlobPhoto(byte[] photo) {
        this.blobPhoto = photo;
    }
}
