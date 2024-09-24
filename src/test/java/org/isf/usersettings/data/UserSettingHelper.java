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
package org.isf.usersettings.data;

import java.util.List;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import org.isf.usersettings.dto.UserSettingDTO;
import org.isf.menu.model.UserSetting;

/**
 * Helper class to generate DTOs and Entities for users endpoints test
 *
 * @author Silevester D.
 * @since 1.15
 */
public class UserSettingHelper {

	public static UserSetting generate() {
		UserSetting userSetting = new UserSetting();
		userSetting.setUser("contrib");
		userSetting.setConfigName("test.config");
		userSetting.setConfigValue("test config value");

		return userSetting;
	}

	public static List<UserSetting> generateMany(int nb) {
		return IntStream.range(0, nb).mapToObj(i -> {
			UserSetting userSetting = new UserSetting();
			userSetting.setUser("contrib");
			userSetting.setConfigName("test.config " + i);
			userSetting.setConfigValue("test config " + i + " value");

			return userSetting;
		}).toList();
	}

	public static String asJsonString(List<UserSettingDTO> userSettingDTOS) {
		try {
			return new ObjectMapper()
				.registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.writeValueAsString(userSettingDTOS);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
