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
package org.isf.menu.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class GroupMenuDTO {

	@Schema(description = "Code of the group menu", example = "1", maxLength = 50)
	private Integer code;

	@NotNull
	@Schema(description = "The related user group's code", example = "labo", maxLength = 50)
	private String userGroup;

	@NotNull
	@Schema(description = "The related menu item's code", example = "admtype")
	private String menuItem;

	public GroupMenuDTO() {
	}

	public GroupMenuDTO(Integer code, String userGroup, String menuItem) {
		this.code = code;
		this.userGroup = userGroup;
		this.menuItem = menuItem;
	}

	public Integer getCode() {
		return this.code;
	}

	public String getUserGroup() {
		return this.userGroup;
	}

	public String getMenuItem() {
		return this.menuItem;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public void setMenuItem(String menuItem) {
		this.menuItem = menuItem;
	}
}
