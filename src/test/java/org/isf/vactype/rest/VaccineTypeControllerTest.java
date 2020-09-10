package org.isf.vactype.rest;

import static org.junit.Assert.fail;

import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.mapper.VaccineTypeMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class VaccineTypeControllerTest {
	private final Logger logger = LoggerFactory.getLogger(VaccineTypeControllerTest.class);
	
	@Mock
	protected VaccineTypeBrowserManager vaccinetypeBrowserManagerMock;
	
	protected VaccineTypeMapper vaccineTypeMapper = new VaccineTypeMapper();
	
	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new VaccineTypeController(vaccinetypeBrowserManagerMock, vaccineTypeMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(vaccineTypeMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testGetVaccineType() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewVaccineType() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateVaccineType() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteVaccineType() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckVaccineTypeCode() {
		fail("Not yet implemented");
	}

}
