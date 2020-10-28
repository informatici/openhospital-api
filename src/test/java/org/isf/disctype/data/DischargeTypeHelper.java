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
package org.isf.disctype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.test.TestDischargeType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DischargeTypeHelper {

	public static DischargeType setup(String code) throws OHException {
		TestDischargeType testDischargeType = new TestDischargeType();
		DischargeType dischargeType = testDischargeType.setup(false);
		dischargeType.setCode(code);
		return dischargeType;
	}

	public static String asJsonString(DischargeTypeDTO dischargeTypeDTO) {
		try {
			return new ObjectMapper().writeValueAsString(dischargeTypeDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<DischargeType> setupDischargeTypeList(int size) {
		return (ArrayList<DischargeType>) IntStream.range(0, size)
				.mapToObj(i -> {
					try {
						return DischargeTypeHelper.setup("" + i);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
	}

}
