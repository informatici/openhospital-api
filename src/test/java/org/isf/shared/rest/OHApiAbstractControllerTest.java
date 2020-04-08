package org.isf.shared.rest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.modelmapper.ModelMapper;

public class OHApiAbstractControllerTest {
	
	ModelMapper modelMapper =  new ModelMapper();
	
	private OHApiAbstractController<Object, Object> target;

	@Test
	public void testOHApiAbstractController() {
		target = createInstance(modelMapper);
		assertNotNull(target);
	}

	private OHApiAbstractController<Object, Object> createInstance(ModelMapper modelMapper) {
		return  new OHApiAbstractController<Object, Object>(modelMapper) {

			@Override
			protected Class getDTOClass() {
				return Object.class;
			}

			@Override
			protected Class getModelClass() {
				return Object.class;
			}
			
		};
	}

	@Test
	public void testToDTO() {
		target = createInstance(modelMapper);
		Object expected = new Object();
		Object dto = target.toDTO(expected);
		assertNotNull(dto);
		assertEquals(expected, dto);
	}

	@Test
	public void testToModel() {
		target = createInstance(modelMapper);
		Object expected = new Object();
		Object model = target.toModel(expected);
		assertNotNull(model);
		assertEquals(expected, model);
	}

	@Test
	public void testToDTOList() {
		List<Object> expectedList  = Arrays.asList(new Object(),new Object());
		target = createInstance(modelMapper);
		List<Object> actualList = target.toDTOList(expectedList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetDTOClass() {
		target = createInstance(modelMapper);
		Class<Object> actualDTOClass = target.getModelClass();
		assertNotNull(actualDTOClass);
		assertEquals(Object.class, actualDTOClass);
	}

	@Test
	public void testGetModelClass() {
		target = createInstance(modelMapper);
		Class<Object> actualModelClass = target.getModelClass();
		assertNotNull(actualModelClass);
		assertEquals(Object.class, actualModelClass);
	}

}
