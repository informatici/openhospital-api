/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.exatype.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ExamTypeDTO {

    @Schema(description = "Exam Type Code", example = "HB")
    private String code;

    @Schema(description = "Exam Type Description", example = "1.Haematology", maxLength = 50)
    private String description;

    public ExamTypeDTO() {
    }

    public ExamTypeDTO(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return "ExamTypeDTO{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}