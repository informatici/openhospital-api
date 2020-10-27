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
import com.fasterxml.jackson.databind.ObjectMapper;

public class FullBillDTOHelper{
	public static FullBillDTO setup(PatientMapper patientMapper, BillItemsMapper billItemsMapper, BillPaymentsMapper billPaymentsMapper) throws OHException{
		Bill bill = BillHelper.setup();
		FullBillDTO fullBillDTO = new  FullBillDTO();
		
		Patient patient = new TestPatient().setup(true);
		PatientDTO patientDTO = patientMapper.map2DTO(patient);
		
		BillDTO billDTO = new BillDTO();
		billDTO.setPatientDTO(patientDTO);
		billDTO.setListName(bill.getListName());
		billDTO.setPatName(patient.getFirstName());
		//OP-205
		//BillDTO billDTO = OHModelMapper.getObjectMapper().map(bill, BillDTO.class);
		fullBillDTO.setBillDTO(billDTO);
		
		TestBillItems tbi = new TestBillItems();
		BillItems billItems = tbi.setup(bill, false);
		BillItemsDTO billItemsDTO = billItemsMapper.map2DTO(billItems);
		fullBillDTO.setBillItemsDTO(Arrays.asList(billItemsDTO));

		TestBillPayments tbp = new TestBillPayments();
		BillPayments billPayments = tbp.setup(bill, false);
		BillPaymentsDTO billPaymentsDTO = billPaymentsMapper.map2DTO(billPayments);
		fullBillDTO.setBillPaymentsDTO(Arrays.asList(billPaymentsDTO));
		
		return fullBillDTO;
	}
	
	public static String asJsonString(FullBillDTO fullBillDTO){
		try {
			return new ObjectMapper().writeValueAsString(fullBillDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String asJsonString(List<FullBillDTO> fullBillDTOList){
		try {
			return new ObjectMapper().writeValueAsString(fullBillDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}