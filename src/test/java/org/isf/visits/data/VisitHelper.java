package org.isf.visits.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.patient.data.PatientHelper;
import org.isf.utils.exception.OHException;
import org.isf.visits.dto.VisitDTO;
import org.isf.visits.model.Visit;
import org.isf.visits.test.TestVisit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VisitHelper {

	public static Visit setup(int id) throws OHException {
		TestVisit testVisits = new TestVisit();
		Visit visit = testVisits.setup(PatientHelper.setup(),false);
		visit.setVisitID(id);
		return visit;
	}

	public static String asJsonString(VisitDTO visitDTO) {
		try {
			return new ObjectMapper().writeValueAsString(visitDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String asJsonString(List<VisitDTO> visitDTOList) {
		try {
			return new ObjectMapper().writeValueAsString(visitDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<Visit> setupVisitList(int size) {
		return (ArrayList<Visit>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return VisitHelper.setup(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}
	
}
