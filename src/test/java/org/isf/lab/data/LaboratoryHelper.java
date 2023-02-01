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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.lab.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.exa.model.Exam;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.model.Laboratory;
import org.isf.lab.test.TestLaboratory;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LaboratoryHelper {
	public static Laboratory setup() throws OHException {
		Patient patient = new Patient();
		boolean usingSet = true;
		Exam exam = new Exam();
		return new TestLaboratory().setup( exam,  patient, usingSet);
	}
	public static Laboratory setup(Integer id) throws OHException {
		Laboratory lab = LaboratoryHelper.setup();
		lab.setCode(id);
		return lab;
	}


	public static String asJsonString(LaboratoryDTO body) {
		// TODO Auto-generated method stub
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List<Laboratory> genList(int n) {

		return IntStream.range(0, n)
				.mapToObj(i -> {
					try {
						return LaboratoryHelper.setup(i);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());

	}

	public static ArrayList<Laboratory> genArrayList(int n) {
		return new ArrayList<>(LaboratoryHelper.genList(n));
	}

}
