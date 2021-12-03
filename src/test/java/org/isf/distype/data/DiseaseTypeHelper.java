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
package org.isf.distype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.disease.model.Disease;
import org.isf.disease.test.TestDisease;
import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.test.TestDiseaseType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DiseaseTypeHelper {

	public static DiseaseType setup(int i) throws OHException {
		TestDiseaseType testDiseaseType = new TestDiseaseType();
		DiseaseType diseaseType = testDiseaseType.setup(false);
		diseaseType.setCode(diseaseType.getCode() + i);
		return diseaseType;
	}

	public static Disease setup(DiseaseType diseaseType) throws OHException {
		TestDisease td = new TestDisease();
		return td.setup(diseaseType, false);
	}

	public static ArrayList<DiseaseType> setupDiseaseTypeList(int size) {
		return (ArrayList<DiseaseType>) IntStream.range(0, size)
				.mapToObj(i -> {
					try {
						return DiseaseTypeHelper.setup(i);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
	}

	public static String asJsonString(DiseaseTypeDTO body) {
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
