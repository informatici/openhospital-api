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
package org.isf.accounting.data;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.mapper.BillItemsMapper;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.test.TestBillItems;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class BillItemsDTOHelper {

	public static BillItemsDTO setup(BillItemsMapper billItemsMapper) throws OHException {
		Bill bill = BillHelper.setup();
		TestBillItems tbi = new TestBillItems();
		BillItems billItems = tbi.setup(bill, false);
		return billItemsMapper.map2DTO(billItems);
	}

	public static String asJsonString(BillItemsDTO billItemsDTO) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(billItemsDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String asJsonString(List<BillItemsDTO> billItemsDTOList) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(billItemsDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<BillItems> toModelList(List<BillItemsDTO> billItemsDTOList, BillItemsMapper billItemsMapper) {
		return billItemsDTOList.stream().map(billItemsMapper::map2Model).collect(Collectors.toList());
	}

}