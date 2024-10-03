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
package org.isf.users.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.menu.model.User;
import org.isf.usergroups.data.UserGroupHelper;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * Helper class to generate DTOs and Entities for users endpoints test
 * 
 * @author Silevester D.
 * @since 1.15
 */
public class UserHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserHelper.class);

	private static final ObjectMapper objectMapper = new ObjectMapper()
					.registerModule(new ParameterNamesModule())
					.registerModule(new Jdk8Module())
					.registerModule(new JavaTimeModule());

	public static User generateUser() throws OHException {
		User user = new User();
		user.setUserGroupName(UserGroupHelper.generateUserGroup());
		user.setUserName("oh user");
		user.setDesc("oh first user");
		user.setPasswd("very strong password");
		return user;
	}

	public static List<User> generateUsers(int nbUsers) throws OHException {
		return IntStream.range(0, nbUsers).mapToObj(i -> {
			try {
				return generateUser();
			} catch (OHException e) {
				LOGGER.error("Error generating user", e);
				return null;
			}
		}).collect(Collectors.toList());
	}

	// TODO: to be moved in a general package?
	public static <T> String asJsonString(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error converting object to JSON", e);
			return null;
		}
	}
}
