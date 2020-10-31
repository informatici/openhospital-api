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
package org.isf.exam.dto;

import org.isf.exatype.dto.ExamTypeDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamDTO {

    @ApiModelProperty(notes = "Exam Code", example = "99.99", position = 1)
    private String code;

    @ApiModelProperty(notes = "Exam Description", example = "99.99 HB", position = 2)
    private String description;

    @ApiModelProperty(notes = "Exam Procedure", example = "1", position = 3)
    private Integer procedure;

    @ApiModelProperty(notes = "Exam Default Result", example = ">=12 (NORMAL)", position = 4)
    private String defaultResult;

    private ExamTypeDTO examtype;

    @ApiModelProperty(notes = "Exam Db Version to increment", example = "0", position = 5)
    private Integer lock;

    public ExamDTO() {
    }

    public ExamDTO(String code, String description, Integer procedure, String defaultResult, ExamTypeDTO examtype, Integer lock) {
        this.code = code;
        this.description = description;
        this.procedure = procedure;
        this.defaultResult = defaultResult;
        this.examtype = examtype;
        this.lock = lock;
    }

	@ApiModelProperty(hidden= true)
    public Integer getLock() {
        return lock;
    }

    @Override
    public String toString() {
        return "ExamDTO{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", procedure=" + procedure +
                ", defaultResult='" + defaultResult + '\'' +
                ", examtype=" + examtype +
                ", lock=" + lock +
                '}';
    }
}