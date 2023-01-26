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

import java.util.Arrays;
import java.util.List;

import org.isf.accounting.dto.BillDTO;
import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.dto.FullBillDTO;
import org.isf.accounting.mapper.BillItemsMapper;
import org.isf.accounting.mapper.BillPaymentsMapper;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.test.TestBillItems;
import org.isf.accounting.test.TestBillPayments;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class FullBillDTOHelper {

	public static FullBillDTO setup(PatientMapper patientMapper, BillItemsMapper billItemsMapper, BillPaymentsMapper billPaymentsMapper) throws OHException {
		Bill bill = BillHelper.setup();
		FullBillDTO fullBillDTO = new FullBillDTO();

		Patient patient = new TestPatient().setup(true);
		PatientDTO patientDTO = patientMapper.map2DTO(patient);

		BillDTO billDTO = new BillDTO();
		billDTO.setPatient(patientDTO);
		billDTO.setListName(bill.getListName());
		billDTO.setPatName(patient.getFirstName());
		//OP-205
		//BillDTO billDTO = OHModelMapper.getObjectMapper().map(bill, BillDTO.class);
		fullBillDTO.setBill(billDTO);

		TestBillItems tbi = new TestBillItems();
		BillItems billItems = tbi.setup(bill, false);
		BillItemsDTO billItemsDTO = billItemsMapper.map2DTO(billItems);
		fullBillDTO.setBillItems(Arrays.asList(billItemsDTO));

		TestBillPayments tbp = new TestBillPayments();
		BillPayments billPayments = tbp.setup(bill, false);
		BillPaymentsDTO billPaymentsDTO = billPaymentsMapper.map2DTO(billPayments);
		fullBillDTO.setBillPayments(Arrays.asList(billPaymentsDTO));

		return fullBillDTO;
	}

	public static String asJsonString(FullBillDTO fullBillDTO) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(fullBillDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String asJsonString(List<FullBillDTO> fullBillDTOList) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(fullBillDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}