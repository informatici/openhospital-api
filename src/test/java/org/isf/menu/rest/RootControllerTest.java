/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.menu.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.OpenHospitalApiApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
class RootControllerTest {

	private MockMvc mockMvc;

	@Mock
	private VersionService versionService;

	@InjectMocks
	private RootController rootController;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(rootController).build();
	}

	@Test
	void testRootEndpoint() throws Exception {
		// Mock the VersionService to return a specific version
		when(versionService.getVersion()).thenReturn("X.Y.Z");

		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.service").value("Open Hospital API"))
			.andExpect(jsonPath("$.version").value("X.Y.Z"))
			.andExpect(jsonPath("$.documentation").value("/swagger-ui/index.html"))
			.andExpect(jsonPath("$.healthcheck").value("/healthcheck"));
	}

	@Test
	void testHealthcheckEndpoint() throws Exception {
		mockMvc.perform(get("/healthcheck"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}
}
