package org.isf.disease.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.rest.AdmissionTypeController;
import org.isf.admtype.rest.AdmissionTypeControllerTest;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.mapper.DiseaseMapper;
import org.isf.disease.model.Disease;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class DiseaseControllerTest {
	private final Logger logger = LoggerFactory.getLogger(DiseaseControllerTest.class);
	
	@Mock
	private DiseaseBrowserManager diseaseBrowserManagerMock;
	
	private DiseaseMapper diseaseMapper= new  DiseaseMapper();

	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DiseaseController(diseaseBrowserManagerMock, diseaseMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(diseaseMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testGetDiseasesOpd() {
		String request = "/diseases/opd";
		
		List<Disease> diseases = new ArrayList<Disease>();
		
		
		AdmissionType admissionType =  new AdmissionType("ZZ","aDescription");
		ArrayList<AdmissionType> admtFounds = new ArrayList<AdmissionType>();
		admtFounds.add(admissionType);
		when(diseaseBrowserManagerMock.getDiseaseOpd())
			.thenReturn(diseases);
		
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesOpdByCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDiseasesIpdOut() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDiseasesIpdOutByCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDiseasesIpdIn() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDiseasesIpdInByCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDiseases() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDiseasesString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllDiseases() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDiseaseByCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewDisease() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDisease() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteDisease() {
		fail("Not yet implemented");
	}

}
