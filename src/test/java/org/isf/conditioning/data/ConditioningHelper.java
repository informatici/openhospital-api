/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.conditioning.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.conditioning.TestConditioning;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.menu.TestUser;
import org.isf.menu.TestUserGroup;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class ConditioningHelper {

	private static ObjectMapper objectMapper;

	public static Conditioning setup() throws OHException {
		TestPatient testPatient = new TestPatient();
		Patient patient = testPatient.setup(false);
		TestUserGroup testUserGroup = new TestUserGroup();
		UserGroup userGroup = testUserGroup.setup(false);
		TestUser testUser = new TestUser();
		User user = testUser.setup(userGroup, false);
		TestConditioning testConditioning = new TestConditioning();
		return testConditioning.setup(patient, user, false);
	}

	public static List<Conditioning> setupConditioningList(int size) {
		return IntStream.range(0, size)
			.mapToObj(i -> {
				try {
					return ConditioningHelper.setup();
				} catch (OHException e) {
					e.printStackTrace();
				}
				return null;
			})
			.collect(Collectors.toList());
	}

	public static String asJsonString(ConditioningDTO conditioningDTO) {
		try {
			return getObjectMapper().writeValueAsString(conditioningDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String asJsonString(List<?> list) {
		try {
			return getObjectMapper().writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ConditioningDTO setup(ConditioningMapper conditioningMapper) throws OHException {
		return conditioningMapper.map2DTO(ConditioningHelper.setup());
	}

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper()
				.registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
		}
		return objectMapper;
	}

}