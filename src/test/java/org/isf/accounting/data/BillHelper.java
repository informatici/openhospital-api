package org.isf.accounting.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.test.TestBill;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.test.TestPriceList;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillHelper{
	public static Bill setup() throws OHException{
		TestPatient testPatient =  new TestPatient();
		Patient patient = testPatient.setup(false); 
		TestPriceList testPriceList =  new TestPriceList();
		PriceList priceList = testPriceList.setup(false);
		TestBill testBill = new TestBill();
		Bill bill = testBill.setup(priceList, patient, false);
		return bill;
	}
	
	public static Bill setup(Integer id) throws OHException {
		Bill bill = BillHelper.setup();
		bill.setId(id);
		return bill;
	}

	public static String asJsonString(Bill bill){
		try {
			return new ObjectMapper().writeValueAsString(bill);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Bill> genList(int n) {
		
		return IntStream.range(0, n)
				.mapToObj(i -> {	try {
										return BillHelper.setup(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
		
	}
	
	public static ArrayList<Bill>  genArrayList(int n){
		return new ArrayList<Bill>(BillHelper.genList(n));
	}
}