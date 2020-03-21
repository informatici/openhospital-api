package org.isf.ward.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Class representing a ward")
public class WardDTO {

    @NotNull
    @ApiModelProperty(notes = "Code of the ward", example="M", position = 1)
    private String code;

    @NotNull
    @ApiModelProperty(notes = "Description of the ward", example="MATERNITY", position = 1)
    private String description;

    @ApiModelProperty(notes = "Telephone of the ward", example="234/52544", position = 1)
    private String telephone;

    @ApiModelProperty(notes = "Fax of the ward", example="54324/5424", position = 1)
    private String fax;

    @ApiModelProperty(notes = "Email of the ward", example="maternity@stluke.org", position = 1)
    private String email;

    @NotNull
    @ApiModelProperty(notes = "Number of beds of the ward", example="10", position = 1)
    private Integer beds;

    @NotNull
    @ApiModelProperty(notes = "Number of nurses of the ward", example="4", position = 1)
    private Integer nurs;

    @NotNull
    @ApiModelProperty(notes = "Number of doctors of the ward", example="2", position = 1)
    private Integer docs;

    @NotNull
    @ApiModelProperty(notes = "Is the ward a farmacy?", example="true", position = 1)
    private boolean isPharmacy;

    @NotNull
    @ApiModelProperty(notes = "Is the ward for male?", example="true", position = 1)
    private boolean isMale;

    @NotNull
    @ApiModelProperty(notes = "Is the ward for female?", example="true", position = 1)
    private boolean isFemale;

    @Version
    @ApiModelProperty(notes = "Number of lock of the ward", example="1", position = 1)
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

    public void setPharmacy(boolean pharmacy) {
        isPharmacy = pharmacy;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public boolean isFemale() {
        return isFemale;
    }

    public void setFemale(boolean female) {
        isFemale = female;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }
}
