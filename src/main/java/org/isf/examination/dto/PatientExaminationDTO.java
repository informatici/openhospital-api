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
package org.isf.examination.dto;

import java.sql.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientExaminationDTO {

    @ApiModelProperty(notes = "Patient Examination Id", example = "1", position = 1)
    private int pex_ID;

    @ApiModelProperty(notes = "Date of Patient Examination", example = "2020-03-19T14:58:00.000Z", position = 2)
    private Date pex_date;

    @ApiModelProperty(notes = "Patient Examination Code", position = 3)
    private Integer patientCode;

    @ApiModelProperty(notes = "Patient Height in cm", position = 4)
    private Integer pex_height;

    @ApiModelProperty(notes = "Patient Weight in Kg", position = 5)
    private Double pex_weight;

    @ApiModelProperty(notes = "Blood Pressure MIN in mmHg", position = 6)
    private Integer pex_pa_min;

    @ApiModelProperty(notes = "Blood Pressure MAX in mmHg", position = 7)
    private Integer pex_pa_max;

    @ApiModelProperty(notes = "Heart Rate in APm", position = 8)
    private Integer pex_fc;

    @ApiModelProperty(notes = "Patient Temperature in °C", position = 10)
    private Double pex_temp;

    @ApiModelProperty(notes = "Patient Saturation in %", position = 11)
    private Double pex_sat;

    @ApiModelProperty(notes = "Examination Note", position = 12)
    private String pex_note;

}
