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
package org.isf.usergroups.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.isf.permissions.dto.PermissionDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserGroupDTO {

	@NotNull
	@Schema(description = "Name of the group (must be unique)", example = "labo", maxLength = 50)
	private String code;
	@Schema(description = "The description of the group", example = "Staff members working in the laboratory", maxLength = 128)
	private String desc;
	@Schema(description = "Whether the group has been soft deleted or not", example = "false")
	private boolean deleted;

	@Schema(description = "List of group's permissions")
	private List<PermissionDTO> permissions;

	public UserGroupDTO() {
	}

	public UserGroupDTO(String code, String desc) {
		this(code, desc, false);
	}

	public UserGroupDTO(String code, String desc, boolean deleted) {
		this.code = code;
		this.desc = desc;
		this.deleted = deleted;
	}

	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return this.desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<PermissionDTO> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionDTO> permissions) {
		this.permissions = permissions;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
