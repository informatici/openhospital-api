package org.isf.patient.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientHelper{
	
	public static PatientDTO setup(PatientMapper patientMapper) throws OHException{
		Patient patient = setupPatient();
		return patientMapper.map2DTO(patient);
	}
	
	public static String asJsonString(PatientDTO patientDTO){
		try {
			return new ObjectMapper().writeValueAsString(patientDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String asJsonString(List<PatientDTO> patientDTOList){
		try {
			return new ObjectMapper().writeValueAsString(patientDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Patient setupPatient() throws OHException{
		return  new TestPatient().setup(true);
	}
	
	public static ArrayList<Patient> setupPatientList(int size) {
		return (ArrayList<Patient>) IntStream.range(1, size+1)
				.mapToObj(i -> {	Patient ep = null;
									try {
										ep = PatientHelper.setupPatient();
										ep.setCode(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return ep;
								}
				).collect(Collectors.toList());
	}
}