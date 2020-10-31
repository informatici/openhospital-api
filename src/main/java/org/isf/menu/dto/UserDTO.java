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
package org.isf.menu.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

	@NotNull
	@ApiModelProperty(notes="the username (must be unique)", example = "John Doe", position = 1)	
	private String userName;

	@NotNull
	@ApiModelProperty(notes="the user's group", position = 2)
	private UserGroupDTO userGroupName;

	@NotNull
	@ApiModelProperty(notes="the user's password", example = "21@U2g423", position = 3)
	private String passwd;

	@ApiModelProperty(notes="the user's description", example = "Lab chief technician", position = 4)
	private String desc;
	
	public UserDTO() {
	}

	public UserDTO(String userName, UserGroupDTO userGroupName, String passwd, String desc) {
		this.userName = userName;
		this.userGroupName = userGroupName;
		this.passwd = passwd;
		this.desc = desc;
	}

}
