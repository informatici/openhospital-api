/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import jakarta.servlet.ServletException;

import org.isf.OpenHospitalApiApplication;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.ward.manager.WardBrowserManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Verifies the authorization rules added for the billing (/bills) and stock movement (/stockmovements)
 * endpoints, which previously fell through to anyRequest().authenticated(), plus the /wardsNoMaternity
 * endpoint uncovered by the fall-through audit. Only the authorization outcome is asserted: a request that
 * is not stopped with 401/403 has passed the filter chain and reached the controller.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
class BillAndStockMovementAuthorizationTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private BillBrowserManager billManager;

	@MockitoBean
	private MovBrowserManager movManager;

	@MockitoBean
	private WardBrowserManager wardManager;

	private void assertReachedController(RequestBuilder request) throws Exception {
		try {
			int status = mvc.perform(request).andReturn().getResponse().getStatus();
			assertThat(status).isNotIn(HttpStatus.UNAUTHORIZED.value(), HttpStatus.FORBIDDEN.value());
		} catch (ServletException e) {
			// The authorization filter never throws: it answers 401/403. Reaching the controller and
			// failing inside it (e.g. mapping an empty test body) still proves the request was authorized.
		}
	}

	// ----- unauthenticated requests are rejected with 401 -----

	@Test
	@DisplayName("Anonymous access to /bills is unauthorized")
	void billsRejectAnonymous() throws Exception {
		mvc.perform(get("/bills")).andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Anonymous access to /stockmovements is unauthorized")
	void stockMovementsRejectAnonymous() throws Exception {
		mvc.perform(get("/stockmovements")).andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Anonymous access to /wardsNoMaternity is unauthorized")
	void wardsNoMaternityRejectAnonymous() throws Exception {
		mvc.perform(get("/wardsNoMaternity")).andExpect(status().isUnauthorized());
	}

	// ----- bills: reading -----

	@Test
	@WithMockUser(username = "admin", authorities = { "bills.read" })
	@DisplayName("Reading bills is allowed with bills.read")
	void readBillsAllowed() throws Exception {
		assertReachedController(get("/bills"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "bills.create" })
	@DisplayName("Reading bills is forbidden without bills.read")
	void readBillsForbidden() throws Exception {
		mvc.perform(get("/bills")).andExpect(status().isForbidden());
	}

	// ----- bills: the search endpoints use POST but are read operations -----

	@Test
	@WithMockUser(username = "admin", authorities = { "bills.read" })
	@DisplayName("Searching bills is allowed with bills.read")
	void searchBillsAllowed() throws Exception {
		assertReachedController(post("/bills/search/by/item")
			.contentType(MediaType.APPLICATION_JSON).content("{}"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "bills.create" })
	@DisplayName("Searching bills is forbidden without bills.read")
	void searchBillsForbidden() throws Exception {
		mvc.perform(post("/bills/search/by/item").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isForbidden());
	}

	// ----- bills: mutating -----

	@Test
	@WithMockUser(username = "admin", authorities = { "bills.create" })
	@DisplayName("Creating a bill is allowed with bills.create")
	void createBillAllowed() throws Exception {
		assertReachedController(post("/bills")
			.contentType(MediaType.APPLICATION_JSON).content("{}"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "bills.read" })
	@DisplayName("Creating a bill is forbidden without bills.create")
	void createBillForbidden() throws Exception {
		mvc.perform(post("/bills").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "bills.read" })
	@DisplayName("Deleting a bill is forbidden without bills.delete")
	void deleteBillForbidden() throws Exception {
		mvc.perform(delete("/bills/1")).andExpect(status().isForbidden());
	}

	// ----- stock movements: reading -----

	@Test
	@WithMockUser(username = "admin", authorities = { "stockmovements.read" })
	@DisplayName("Reading stock movements is allowed with stockmovements.read")
	void readStockMovementsAllowed() throws Exception {
		when(movManager.getMovements()).thenReturn(List.of());
		assertReachedController(get("/stockmovements"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "stockmovements.create" })
	@DisplayName("Reading stock movements is forbidden without stockmovements.read")
	void readStockMovementsForbidden() throws Exception {
		mvc.perform(get("/stockmovements")).andExpect(status().isForbidden());
	}

	// ----- stock movements: mutating -----

	@Test
	@WithMockUser(username = "admin", authorities = { "stockmovements.create" })
	@DisplayName("Charging stock is allowed with stockmovements.create")
	void chargeStockMovementsAllowed() throws Exception {
		assertReachedController(post("/stockmovements/charge")
			.contentType(MediaType.APPLICATION_JSON).content("[]"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "stockmovements.read" })
	@DisplayName("Charging stock is forbidden without stockmovements.create")
	void chargeStockMovementsForbidden() throws Exception {
		mvc.perform(post("/stockmovements/charge").contentType(MediaType.APPLICATION_JSON).content("[]"))
			.andExpect(status().isForbidden());
	}

	// ----- wardsNoMaternity (fall-through audit fix) -----

	@Test
	@WithMockUser(username = "admin", authorities = { "wards.read" })
	@DisplayName("Reading wardsNoMaternity is allowed with wards.read")
	void wardsNoMaternityAllowed() throws Exception {
		when(wardManager.getWardsNoMaternity()).thenReturn(List.of());
		assertReachedController(get("/wardsNoMaternity"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "wards.update" })
	@DisplayName("Reading wardsNoMaternity is forbidden without wards.read")
	void wardsNoMaternityForbidden() throws Exception {
		mvc.perform(get("/wardsNoMaternity")).andExpect(status().isForbidden());
	}
}
