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
package org.isf.stats.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.stat.dto.ReportLauncherDto;
import org.isf.stat.manager.JasperReportsManager;
import org.isf.stats.mapper.ReportLauncherMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ReportsControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportsControllerTest.class);

	@Mock
	protected JasperReportsManager reportsManagerMock;

	protected ReportLauncherMapper reportLauncherMapper = new ReportLauncherMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new ReportsController(reportsManagerMock, reportLauncherMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ReflectionTestUtils.setField(reportLauncherMapper, "modelMapper", new ModelMapper());
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetReportsList_200() throws Exception {
		String request = "/reports";

		ReportLauncherDto report = new ReportLauncherDto("rpt_stat", "POI_ByAgeBySex", "Patients by age and sex", List.of("month", "year"));
		when(reportsManagerMock.getReportsList()).thenReturn(List.of(report));

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].folder").value("rpt_stat"))
			.andExpect(jsonPath("$[0].fileName").value("POI_ByAgeBySex"))
			.andExpect(jsonPath("$[0].title").value("Patients by age and sex"))
			.andExpect(jsonPath("$[0].userInputParameters[0]").value("month"))
			.andExpect(jsonPath("$[0].userInputParameters[1]").value("year"))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetReportsList_emptyList_200() throws Exception {
		String request = "/reports";

		when(reportsManagerMock.getReportsList()).thenReturn(List.of());

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("[]")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}
}
