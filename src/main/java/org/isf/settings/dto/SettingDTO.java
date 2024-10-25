/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.settings.dto;

import jakarta.validation.constraints.NotNull;

import org.isf.settings.model.SettingCategory;
import org.isf.settings.model.SettingValueType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Setting DTO
 * @author Silevester D.
 */
public class SettingDTO {
	@Schema(description = "Setting ID", example = "1")
	@NotNull
	private int id;

	@Schema(description="Setting code", maxLength = 50, example = "AUTOMATICLOT_IN")
	@NotNull
	private String code;

	@Schema(description = "Setting category", example = "application")
	@NotNull
	private SettingCategory category;

	@Schema(description = "value type", example = "bool")
	@NotNull
	private SettingValueType type;

	@Schema(description = "Comma-separated list of possible values", maxLength = 500)
	private String valueOptions;

	@Schema(description="Default value", example = "TRUE")
	@NotNull
	private String defaultValue;

	@Schema(description="The value of the setting", example = "FALSE")
	private String value;

	@Schema(name="Description", maxLength = 500)
	private String description;

	@Schema(description="Whether the app needs restart after the setting has been modified", example = "true")
	private Boolean needRestart = true;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SettingValueType getType() {
		return type;
	}

	public void setType(SettingValueType type) {
		this.type = type;
	}

	public String getValueOptions() {
		return valueOptions;
	}

	public void setValueOptions(String valueOptions) {
		this.valueOptions = valueOptions;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getNeedRestart() {
		return needRestart;
	}

	public void setNeedRestart(Boolean needRestart) {
		this.needRestart = needRestart;
	}

	public SettingCategory getCategory() {
		return category;
	}

	public void setCategory(SettingCategory category) {
		this.category = category;
	}
}
