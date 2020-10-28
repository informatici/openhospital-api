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
package org.isf.accounting.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.mapper.BillPaymentsMapper;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.test.TestBillPayments;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillPaymentsDTOHelper {

	public static BillPaymentsDTO setup(BillPaymentsMapper billPaymentsMapper) throws OHException {
		Bill bill = BillHelper.setup();
		TestBillPayments testBillPayments = new TestBillPayments();
		BillPayments billPayments = testBillPayments.setup(bill, true);
		BillPaymentsDTO billPaymentsDTO = billPaymentsMapper.map2DTO(billPayments);
		return billPaymentsDTO;
	}

	public static String asJsonString(BillPaymentsDTO billPaymentsDTO) {
		try {
			return new ObjectMapper().writeValueAsString(billPaymentsDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String asJsonString(List<BillPaymentsDTO> billPaymentsDTOList) {
		try {
			return new ObjectMapper().writeValueAsString(billPaymentsDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<BillPayments> toModelList(List<BillPaymentsDTO> billPaymentsDTOList, BillPaymentsMapper billPaymentsMapper) {
		ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>(
				billPaymentsDTOList.stream().map(pay -> billPaymentsMapper.map2Model(pay)).collect(Collectors.toList()));
		return billPayments;
	}

	public static List<BillPaymentsDTO> genList(int n, BillPaymentsMapper billPaymentsMapper) throws OHException {
		return IntStream.range(0, n)
				.mapToObj(i -> {
					try {
						return BillPaymentsDTOHelper.setup(billPaymentsMapper);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
	}

	public static ArrayList<BillPaymentsDTO> genArrayList(int n, BillPaymentsMapper billPaymentsMapper) throws OHException {
		return new ArrayList<BillPaymentsDTO>(genList(n, billPaymentsMapper));
	}

	public static ArrayList<BillPayments> genListModel(int n, BillPaymentsMapper billPaymentsMapper) throws OHException {
		return BillPaymentsDTOHelper.toModelList(BillPaymentsDTOHelper.genList(n, billPaymentsMapper), billPaymentsMapper);
	}

}