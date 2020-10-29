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
package org.isf.hospital.dto;

import io.swagger.annotations.ApiModelProperty;

public class HospitalDTO {

    @ApiModelProperty(notes = "Hospital Code", example = "STLUKE", position = 1)
    private String code;

    @ApiModelProperty(notes = "Hospital Description", example = "St. Luke HOSPITAL - Angal", position = 2)
    private String description;

    @ApiModelProperty(notes = "Hospital Address", example = "Hospital Address", position = 3)
    private String address;

    @ApiModelProperty(notes = "Hospital City", example = "Hospital City", position = 4)
    private String city;

    @ApiModelProperty(notes = "Hospital Telephone", example = "+123 0123456789", position = 5)
    private String telephone;

    @ApiModelProperty(notes = "Hospital Fax", example = "+123 0123456789", position = 6)
    private String fax;

    @ApiModelProperty(notes = "Hospital Email", example = "hospital@isf.email.xx", position = 7)
    private String email;

    @ApiModelProperty(notes = "Hospital Currency Cod", example = "EUR", position = 8)
    private String currencyCod;

    @ApiModelProperty(notes = "Hospital Version to increment", example = "0", position = 9)
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

    public String getCurrencyCod() {
        return currencyCod;
    }

    public void setCurrencyCod(String currencyCod) {
        this.currencyCod = currencyCod;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }
}
