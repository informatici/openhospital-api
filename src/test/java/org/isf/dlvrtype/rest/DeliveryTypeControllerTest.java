/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.dlvrtype.rest;

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

import org.isf.dlvrtype.data.DeliveryTypeHelper;
import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.mapper.DeliveryTypeMapper;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class DeliveryTypeControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DeliveryTypeControllerTest.class);

	@Mock
	protected DeliveryTypeBrowserManager deliveryTypeBrowserManagerMock;

	protected DeliveryTypeMapper deliveryTypeMapper = new DeliveryTypeMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DeliveryTypeController(deliveryTypeBrowserManagerMock, deliveryTypeMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(deliveryTypeMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testNewDeliveryType_201() throws Exception {
		String request = "/deliverytypes";
		int code = 123;
		DeliveryType deliveryType = DeliveryTypeHelper.setup(code);
		DeliveryTypeDTO body = deliveryTypeMapper.map2DTO(deliveryType);

		List<DeliveryType> results = new ArrayList<>();
		results.add(deliveryType);

		when(deliveryTypeBrowserManagerMock.getDeliveryType())
				.thenReturn(results);

		boolean isCreated = true;
		when(deliveryTypeBrowserManagerMock.newDeliveryType(deliveryTypeMapper.map2Model(body)))
				.thenReturn(isCreated);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DeliveryTypeHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateDeliveryTypet_200() throws Exception {
		String request = "/deliverytypes/";
		int code = 456;

		DeliveryType deliveryType = DeliveryTypeHelper.setup(code);
		DeliveryTypeDTO body = deliveryTypeMapper.map2DTO(deliveryType);

		when(deliveryTypeBrowserManagerMock.isCodePresent(body.getCode()))
				.thenReturn(true);

		boolean isUpdated = true;
		when(deliveryTypeBrowserManagerMock.updateDeliveryType(deliveryTypeMapper.map2Model(body)))
				.thenReturn(isUpdated);

		MvcResult result = this.mockMvc
				.perform(put(request, "ZZ" + code)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DeliveryTypeHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDeliveryTypes_200() throws Exception {
		String request = "/deliverytypes";

		List<DeliveryType> results = DeliveryTypeHelper.setupDeliveryTypeList(3);

		List<DeliveryTypeDTO> dlvrrestTypeDTOs = deliveryTypeMapper.map2DTOList(results);

		when(deliveryTypeBrowserManagerMock.getDeliveryType())
				.thenReturn(results);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DeliveryTypeHelper.getObjectMapper().writeValueAsString(dlvrrestTypeDTOs))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteDeliveryType_200() throws Exception {
		String request = "/deliverytypes/{code}";

		DeliveryTypeDTO body = deliveryTypeMapper.map2DTO(DeliveryTypeHelper.setup(0));
		String code = body.getCode();

		when(deliveryTypeBrowserManagerMock.isCodePresent(code))
				.thenReturn(true);

		when(deliveryTypeBrowserManagerMock.getDeliveryType())
				.thenReturn(DeliveryTypeHelper.setupDeliveryTypeList(1));

		when(deliveryTypeBrowserManagerMock.deleteDeliveryType(deliveryTypeMapper.map2Model(body)))
				.thenReturn(true);

		String isDeleted = "true";
		MvcResult result = this.mockMvc
				.perform(delete(request, code))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(isDeleted)))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
