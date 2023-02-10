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
import java.util.stream.IntStream;

import org.isf.accounting.model.Bill;
import org.isf.accounting.test.TestBill;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.test.TestPriceList;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class BillHelper {

	private static ObjectMapper objectMapper;

	public static Bill setup() throws OHException {
		TestPatient testPatient = new TestPatient();
		Patient patient = testPatient.setup(false);
		TestPriceList testPriceList = new TestPriceList();
		PriceList priceList = testPriceList.setup(false);
		TestBill testBill = new TestBill();
		return testBill.setup(priceList, patient, null, false);
	}

	public static Bill setup(Integer id) throws OHException {
		Bill bill = BillHelper.setup();
		bill.setId(id);
		return bill;
	}

	public static String asJsonString(Bill bill) {
		try {
			return getObjectMapper().writeValueAsString(bill);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Bill> genList(int n) {

		return IntStream.range(0, n)
				.mapToObj(i -> {
					try {
						return BillHelper.setup(i);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
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
