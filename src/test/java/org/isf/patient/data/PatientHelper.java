package org.isf.patient.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.admission.data.AdmissionHelper;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientHelper{
	
	public static PatientDTO setup(PatientMapper patientMapper) throws OHException{
		Patient patient = setup();
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

	public static Patient setup() throws OHException{
		return  new TestPatient().setup(true);
	}
	
	public static ArrayList<Patient> setupPatientList(int size) {
		return (ArrayList<Patient>) IntStream.range(1, size+1)
				.mapToObj(i -> {	Patient ep = null;
									try {
										ep = PatientHelper.setup();
										ep.setCode(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return ep;
								}
				).collect(Collectors.toList());
	}

	public static ArrayList<AdmittedPatient> setupAdmittedPatientList(int size) {
		ArrayList<Patient> patientList = setupPatientList(size);
		ArrayList<Admission> admissionList = AdmissionHelper.setupAdmissionList(size);
		return (ArrayList<AdmittedPatient>) IntStream.range(0, size)
				
				.mapToObj(i ->new AdmittedPatient(patientList.get(i),admissionList.get(i))
								
				).collect(Collectors.toList());
	}
	
	public static String asJsonString(List<?> list){
		try {
			return new ObjectMapper().writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}