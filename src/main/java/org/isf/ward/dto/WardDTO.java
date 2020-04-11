package org.isf.ward.dto;

import javax.validation.constraints.NotNull;

public class WardDTO {

	private String code;

	@NotNull
	private String description;

	private String telephone;

	private String fax;

	private String email;

	@NotNull
	private Integer beds;

	@NotNull
	private Integer nurs;

	@NotNull
	private Integer docs;

	@NotNull
	private boolean isPharmacy;

	@NotNull
	private boolean isMale;

	@NotNull
	private boolean isFemale;

	private Integer lock;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getBeds() {
		return beds;
	}

	public void setBeds(Integer beds) {
		this.beds = beds;
	}

	public Integer getNurs() {
		return nurs;
	}

	public void setNurs(Integer nurs) {
		this.nurs = nurs;
	}

	public Integer getDocs() {
		return docs;
	}

	public void setDocs(Integer docs) {
		this.docs = docs;
	}

	public boolean isPharmacy() {
		return isPharmacy;
	}

	public void setPharmacy(boolean isPharmacy) {
		this.isPharmacy = isPharmacy;
	}

	public boolean isMale() {
		return isMale;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	public boolean isFemale() {
		return isFemale;
	}

	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}
}
