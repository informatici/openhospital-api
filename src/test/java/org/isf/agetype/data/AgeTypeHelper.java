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
package org.isf.agetype.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.model.AgeType;
import org.isf.agetype.test.TestAgeType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class AgeTypeHelper {

	private static ObjectMapper objectMapper;

	public static AgeType setup() throws OHException {
		TestAgeType testAgeType = new TestAgeType();
		return testAgeType.setup(false);
	}

	public static AgeType setup(Integer id) throws OHException {
		AgeType ageType = AgeTypeHelper.setup();
		ageType.setCode(ageType.getCode() + id);
		return ageType;
	}

	public static List<AgeType> genList(int n) {
		return IntStream.range(0, n)
				.mapToObj(i -> {
					try {
						return AgeTypeHelper.setup(i);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
	}

	public static List<AgeType> genArrayList(int n) {
		return AgeTypeHelper.genList(n);
	}

	public static String asJsonString(AgeTypeDTO ageTypeDTO) {
		try {
			return getObjectMapper().writeValueAsString(ageTypeDTO);
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
