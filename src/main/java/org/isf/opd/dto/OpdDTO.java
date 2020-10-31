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
import lombok.Getter;
import lombok.Setter;

/**
 * @author gildas
 */
@Getter
@Setter
public class OpdDTO {

    private int code;

    @ApiModelProperty(notes = "the date of the admission", position = 2)
    private Date date;

    @NotNull
    @ApiModelProperty(notes = "the visit date", position = 3)
    private Date visitDate;

    @ApiModelProperty(notes = "the next visit date", position = 4)
    private Date nextVisitDate;

    @ApiModelProperty(notes = "the admitted patient code", position = 5)
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

	@ApiModelProperty(hidden= true)
	public int getLock() {
		return lock;
	}

    @ApiModelProperty(hidden = true)
    public int getHashCode() {
        return hashCode;
    }

}
