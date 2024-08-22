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
package org.isf.menu.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.isf.menu.TestUserGroup;
import org.isf.menu.dto.UserGroupDTO;
import org.isf.menu.model.UserGroup;
import org.isf.utils.exception.OHException;

import java.util.List;

/**
 * Helper class to generate DTOs and Entities for users endpoints test
 *
 * @author Silevester D.
 * @since 1.15
 */
public class UserGroupHelper {
	public static UserGroup generateUserGroup() throws OHException {
		return new TestUserGroup().setup(false);
	}

	public static String asJsonString(UserGroupDTO userGroupDTO) {
		try {
			return new ObjectMapper()
					.registerModule(new ParameterNamesModule())
					.registerModule(new Jdk8Module())
					.registerModule(new JavaTimeModule())
					.writeValueAsString(userGroupDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String asJsonString(List<UserGroupDTO> userGroupDTOs) {
		try {
			return new ObjectMapper()
					.registerModule(new ParameterNamesModule())
					.registerModule(new Jdk8Module())
					.registerModule(new JavaTimeModule())
					.writeValueAsString(userGroupDTOs);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
