/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import java.util.stream.IntStream;

import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.mapper.BillPaymentsMapper;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.test.TestBillPayments;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class BillPaymentsDTOHelper {

	public static BillPaymentsDTO setup(BillPaymentsMapper billPaymentsMapper) throws OHException {
		Bill bill = BillHelper.setup();
		TestBillPayments testBillPayments = new TestBillPayments();
		BillPayments billPayments = testBillPayments.setup(bill, true);
		return billPaymentsMapper.map2DTO(billPayments);
	}

	public static String asJsonString(BillPaymentsDTO billPaymentsDTO) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(billPaymentsDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String asJsonString(List<BillPaymentsDTO> billPaymentsDTOList) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(billPaymentsDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<BillPayments> toModelList(List<BillPaymentsDTO> billPaymentsDTOList, BillPaymentsMapper billPaymentsMapper) {
		return billPaymentsDTOList.stream().map(billPaymentsMapper::map2Model).collect(Collectors.toList());
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

	public static List<BillPayments> genListModel(int n, BillPaymentsMapper billPaymentsMapper) throws OHException {
		return BillPaymentsDTOHelper.toModelList(BillPaymentsDTOHelper.genList(n, billPaymentsMapper), billPaymentsMapper);
	}

}