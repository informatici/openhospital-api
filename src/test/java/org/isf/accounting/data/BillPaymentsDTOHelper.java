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

public class BillPaymentsDTOHelper{
	public static BillPaymentsDTO setup(BillPaymentsMapper billPaymentsMapper ) throws OHException{
		Bill bill = BillHelper.setup();
		TestBillPayments testBillPayments =  new TestBillPayments();
		BillPayments billPayments = testBillPayments.setup(bill, true);
		BillPaymentsDTO billPaymentsDTO =billPaymentsMapper.map2DTO(billPayments);
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
	
	public static ArrayList<BillPayments> toModelList(List<BillPaymentsDTO> billPaymentsDTOList, BillPaymentsMapper billPaymentsMapper){
        ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>(billPaymentsDTOList.stream().map(pay-> billPaymentsMapper.map2Model(pay)).collect(Collectors.toList()));
        return billPayments;
	}
	
	public static List<BillPaymentsDTO> genList(int n, BillPaymentsMapper billPaymentsMapper) throws OHException{
		return IntStream.range(0, n)
			.mapToObj(i -> {	try {
									return BillPaymentsDTOHelper.setup(billPaymentsMapper);
								} catch (OHException e) {
									e.printStackTrace();
								}
								return null;
							}).collect(Collectors.toList());
	}
	
	public static ArrayList<BillPaymentsDTO> genArrayList(int n, BillPaymentsMapper billPaymentsMapper) throws OHException{
		return new ArrayList<BillPaymentsDTO>(genList(n, billPaymentsMapper));
	}

	public static ArrayList<BillPayments> genListModel(int n, BillPaymentsMapper billPaymentsMapper) throws OHException {
		return BillPaymentsDTOHelper.toModelList(BillPaymentsDTOHelper.genList(n, billPaymentsMapper), billPaymentsMapper);
	}
	
}