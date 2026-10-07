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
package org.isf.profession.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.profession.TestProfession;
import org.isf.profession.dto.ProfessionDTO;
import org.isf.profession.model.Profession;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class ProfessionHelper {

	private static ObjectMapper objectMapper;

	public static Profession setup(int i) throws OHException {
		TestProfession testProfession = new TestProfession();
		Profession profession = testProfession.setup(false);
		profession.setCode(profession.getCode() + i);
		return profession;
	}

	public static List<Profession> setupProfessionList(int size) {
		return IntStream.range(0, size)
				.mapToObj(i -> {
					try {
						return ProfessionHelper.setup(i);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
	}

	public static String asJsonString(ProfessionDTO body) {
		try {
			return getObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
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
