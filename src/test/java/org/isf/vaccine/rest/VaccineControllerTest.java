package org.isf.vaccine.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.vaccine.data.VaccineHelper;
import org.isf.vaccine.dto.VaccineDTO;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.mapper.VaccineMapper;
import org.isf.vaccine.model.Vaccine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VaccineControllerTest {
	private final Logger logger = LoggerFactory.getLogger(VaccineControllerTest.class);
	
	@Mock
	protected VaccineBrowserManager vaccineBrowserManagerMock;
	
	protected VaccineMapper vaccineMapper = new VaccineMapper();
	
	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new VaccineController(vaccineBrowserManagerMock, vaccineMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(vaccineMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testGetVaccines_200() throws JsonProcessingException, Exception {
		String request = "/vaccines";
		
		String code = "AA";
		ArrayList<Vaccine> vaccinesList = VaccineHelper.setupVaccineList(4);

		when(vaccineBrowserManagerMock.getVaccine())
			.thenReturn(vaccinesList);
		
		List<VaccineDTO> expectedVaccineDTOs = vaccineMapper.map2DTOList(vaccinesList);
		
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(expectedVaccineDTOs))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetVaccinesByVaccineTypeCode_200() throws JsonProcessingException, Exception {
		String request = "/vaccines/{vaccineTypeCode}";
				
		ArrayList<Vaccine> vaccinesList = VaccineHelper.setupVaccineList(4);
		String vaccineTypeCode = vaccinesList.get(0).getVaccineType().getCode();
		
		when(vaccineBrowserManagerMock.getVaccine(vaccineTypeCode))
			.thenReturn(vaccinesList);
		
		MvcResult result = this.mockMvc
			.perform(get(request, vaccineTypeCode))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(new ObjectMapper().writeValueAsString(vaccineMapper.map2DTOList(vaccinesList))))
			.andReturn();
		
		logger.debug("result: {}", result);	
	}

	@Test
	public void testNewVaccine_201() throws Exception {
		String request = "/vaccines";
		String code = "ZZ";
		VaccineDTO  body = vaccineMapper.map2DTO(VaccineHelper.setup(code));
		
		boolean isCreated = true;
		when(vaccineBrowserManagerMock.newVaccine(vaccineMapper.map2Model(body)))
			.thenReturn(isCreated);
		
		MvcResult result = this.mockMvc
			.perform(post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(VaccineHelper.asJsonString(body))
					)
			.andDo(log())
			//.andDo(print())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateVaccine_200() throws Exception {
		String request = "/vaccines";
		String code = "ZZ";
		VaccineDTO  body = vaccineMapper.map2DTO(VaccineHelper.setup(code));

		boolean isUpdated = true;
		when(vaccineBrowserManagerMock.updateVaccine(vaccineMapper.map2Model(body)))
			.thenReturn(isUpdated);
		
		MvcResult result = this.mockMvc
			.perform(put(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(VaccineHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testDeleteVaccine_200() throws Exception {
		String request = "/vaccines/{code}";
		String basecode = "0";
		
		Vaccine vaccine = VaccineHelper.setup(basecode);
		VaccineDTO  body = vaccineMapper.map2DTO(vaccine);
		String code = body.getCode();
		
		when(vaccineBrowserManagerMock.findVaccine(code))
			.thenReturn(vaccine);

		when(vaccineBrowserManagerMock.deleteVaccine(vaccineMapper.map2Model(body)))
			.thenReturn(true);
		
		String isDeleted = "true";
		MvcResult result = this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(isDeleted)))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testCheckVaccineCode_200() throws JsonProcessingException, Exception {
		String request = "/vaccines/check/{code}";
		
		String code = "AA";
		Vaccine vaccine = VaccineHelper.setup(code);

		when(vaccineBrowserManagerMock.codeControl(vaccine.getCode()))
			.thenReturn(true);
		
		MvcResult result = this.mockMvc
			.perform(get(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string("true"))
			.andReturn();
		
		logger.debug("result: {}", result);	}

}
