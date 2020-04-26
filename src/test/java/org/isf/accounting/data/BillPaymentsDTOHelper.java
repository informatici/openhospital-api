package org.isf.accounting.data;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.test.TestBillPayments;
import org.isf.shared.mapper.OHModelMapper;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillPaymentsDTOHelper{
	public static BillPaymentsDTO setup() throws OHException{
		Bill bill = BillHelper.setup();
		TestBillPayments testBillPayments =  new TestBillPayments();
		BillPayments billPayments = testBillPayments.setup(bill, true);
		BillPaymentsDTO billPaymentsDTO = OHModelMapper.getObjectMapper().map(billPayments, BillPaymentsDTO.class);
		return billPaymentsDTO;
	}
	
	public static String asJsonString(BillPaymentsDTO billPaymentsDTO){
		try {
			return new ObjectMapper().writeValueAsString(billPaymentsDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String asJsonString(List<BillPaymentsDTO> billPaymentsDTOList){
		try {
			return new ObjectMapper().writeValueAsString(billPaymentsDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<BillPayments> toModelList(List<BillPaymentsDTO> billPaymentsDTOList){
        ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>(billPaymentsDTOList.stream().map(pay-> getObjectMapper().map(pay, BillPayments.class)).collect(Collectors.toList()));
        return billPayments;
	}
	
	public static List<BillPaymentsDTO> genList(int n) throws OHException{
		return IntStream.range(0, n)
			.mapToObj(i -> {	try {
									return BillPaymentsDTOHelper.setup();
								} catch (OHException e) {
									e.printStackTrace();
								}
								return null;
							}).collect(Collectors.toList());
	}
	
}