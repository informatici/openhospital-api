package org.isf.lab.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.accounting.data.BillHelper;
import org.isf.accounting.model.Bill;
import org.isf.exa.model.Exam;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.model.Laboratory;
import org.isf.lab.test.TestLaboratory;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LaboratoryHelper {
	public static Laboratory setup() throws OHException {
		Patient patient = new Patient();
		boolean usingSet = true;
		Exam exam = new Exam();
		return new TestLaboratory().setup( exam,  patient, usingSet);
	}
	public static Laboratory setup(Integer id) throws OHException {
		Laboratory lab = LaboratoryHelper.setup();
		lab.setCode(id);
		return lab;
	}


	public static String asJsonString(LaboratoryDTO body) {
		// TODO Auto-generated method stub
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List<Laboratory> genList(int n) {

		return IntStream.range(0, n)
				.mapToObj(i -> {
					try {
						return LaboratoryHelper.setup(i);
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());

	}

	public static ArrayList<Laboratory> genArrayList(int n) {
		return new ArrayList<>(LaboratoryHelper.genList(n));
	}

}
