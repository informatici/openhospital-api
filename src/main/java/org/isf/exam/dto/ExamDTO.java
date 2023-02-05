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
    
    @ApiModelProperty(notes = "lock", example = "0")
	private int lock;

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public ExamDTO() {
	}

    public ExamDTO(String code, String description, Integer procedure, String defaultResult, ExamTypeDTO examtype) {
        this.code = code;
        this.description = description;
        this.procedure = procedure;
        this.defaultResult = defaultResult;
        this.examtype = examtype;
    }


    @Override
    public String toString() {
        return "ExamDTO{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", procedure=" + procedure +
                ", defaultResult='" + defaultResult + '\'' +
                ", examtype=" + examtype +
                '}';
    }

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getProcedure() {
		return this.procedure;
	}

	public String getDefaultResult() {
		return this.defaultResult;
	}

	public ExamTypeDTO getExamtype() {
		return this.examtype;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProcedure(Integer procedure) {
		this.procedure = procedure;
	}

	public void setDefaultResult(String defaultResult) {
		this.defaultResult = defaultResult;
	}

	public void setExamtype(ExamTypeDTO examtype) {
		this.examtype = examtype;
	}

}