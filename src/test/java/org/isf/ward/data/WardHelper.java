package org.isf.ward.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.accounting.data.BillHelper;
import org.isf.accounting.model.Bill;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWard;

public class WardHelper {
	
	public static Ward setup(int id) throws OHException {
		TestWard testWard = new TestWard();
		Ward ward = testWard.setup(false);
		//ward.setCode(ward.getCode()+id);
		return ward;
	}

	public static ArrayList<Ward> setupWardList(int size) {
		return (ArrayList<Ward>) IntStream.range(0, size)
					.mapToObj(i -> {	try {
											return WardHelper.setup(i);
										} catch (OHException e) {
											e.printStackTrace();
										}
										return null;
									}).collect(Collectors.toList());
			
	}

}
