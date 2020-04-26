package org.isf.accounting.data;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.test.TestBillItems;
import org.isf.shared.mapper.OHModelMapper;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillItemsDTOHelper{
	public static BillItemsDTO setup() throws OHException{
		Bill bill = BillHelper.setup();
		TestBillItems tbi = new TestBillItems();
		BillItems billItems = tbi.setup(bill, false);
		BillItemsDTO billItemsDTO = OHModelMapper.getObjectMapper().map(billItems, BillItemsDTO.class);
		return billItemsDTO;
	}
	
	public static String asJsonString(BillItemsDTO billItemsDTO){
		try {
			return new ObjectMapper().writeValueAsString(billItemsDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String asJsonString(List<BillItemsDTO> billItemsDTOList){
		try {
			return new ObjectMapper().writeValueAsString(billItemsDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<BillItems> toModelList(List<BillItemsDTO> billItemsDTOList){
        ArrayList<BillItems> billItems = new ArrayList<BillItems>(billItemsDTOList.stream().map(pay-> getObjectMapper().map(pay, BillItems.class)).collect(Collectors.toList()));
        return billItems;
	}
}