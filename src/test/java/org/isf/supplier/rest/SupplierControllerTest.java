/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.isf.supplier.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.isf.OpenHospitalApiApplication;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.mapper.SupplierMapper;
import org.isf.supplier.model.Supplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Supplier Controller Test
 * @author Silevester D.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class SupplierControllerTest {
	private final Logger LOGGER = LoggerFactory.getLogger(SupplierControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SupplierMapper supplierMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private SupplierBrowserManager supplierBrowserManager;

	@Test
	@WithMockUser(username = "admin", authorities = {"suppliers.create"})
	@DisplayName("Successfully create a supplier")
	void testCreateSupplier() throws Exception {
		var supplier = generateSuppliers().stream().findFirst().orElse(null);
		var supplierDTO = supplierMapper.map2DTO(supplier);

		when(supplierBrowserManager.saveOrUpdate(any())).thenReturn(supplier);

		var result = mvc.perform(post("/suppliers")
				.content(objectMapper.writeValueAsString(supplierDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(supplierDTO))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"suppliers.update"})
	@DisplayName("Successfully update a supplier")
	@Disabled("Is throwing 404 and nothing indicates why")
	void testUpdateSupplier() throws Exception {
		var supplier = generateSuppliers().get(0);
		var supplierDTO = supplierMapper.map2DTO(supplier);

		when(supplierBrowserManager.saveOrUpdate(any())).thenReturn(supplier);

		var result = mvc.perform(put("/suppliers")
				.content(objectMapper.writeValueAsString(supplierDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(supplierDTO))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"suppliers.read"})
	@DisplayName("Get all suppliers, deleted ones included")
	void testGetAllSuppliers() throws Exception {
		var suppliers = generateSuppliers();
		var suppliersDTO = supplierMapper.map2DTOList(suppliers);

		when(supplierBrowserManager.getAll()).thenReturn(suppliers);

		var result = mvc.perform(get("/suppliers")
				.queryParam("exclude_deleted", String.valueOf(false))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(suppliersDTO))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"suppliers.read"})
	@DisplayName("Get all suppliers, deleted ones excluded")
	void testGetNonDeletedSuppliers() throws Exception {
		var suppliers = generateSuppliers().stream().filter(sup -> !sup.getSupDeleted().equals('Y')).toList();
		var suppliersDTO = supplierMapper.map2DTOList(suppliers);

		when(supplierBrowserManager.getList()).thenReturn(suppliers);

		var result = mvc.perform(get("/suppliers")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(suppliersDTO))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"suppliers.delete"})
	@DisplayName("Successfully delete a supplier")
	void testDeleteSupplier() throws Exception {
		var supplier = generateSuppliers().stream().findFirst().orElse(null);
		var supplierDTO = supplierMapper.map2DTO(supplier);

		when(supplierBrowserManager.getByID(anyInt())).thenReturn(supplier);

		var result = mvc.perform(delete("/suppliers/{id}", 1)
				.content(objectMapper.writeValueAsString(supplierDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isNoContent())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	private List<Supplier> generateSuppliers() {
		AtomicInteger i = new AtomicInteger();
		return Stream.of('N','N','Y').map(deleted -> {
			i.getAndIncrement();

			return new Supplier(
				i.get(),
				"Supplier " + i,
				"supAddress " + i,
				"supTaxCode " + i,
				"supPhone " + i,
				"supFax " + i,
				"supEmail " + i,
				"supNote " + i,
				deleted
			);
		}).toList();
	}
}
