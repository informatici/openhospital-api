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
package org.isf.medicalstock.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicalstock.data.MovementHelper;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.mapper.LotMapper;
import org.isf.medicalstock.mapper.MovementMapper;
import org.isf.medicalstock.model.Movement;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class StockMovementControllerTest {

	@Mock
	private MovBrowserManager movBrowserManagerMock;

	@Mock
	private MovStockInsertingManager movStockInsertingManagerMock;

	@Mock
	private MedicalBrowsingManager medicalBrowsingManagerMock;

	private final MovementMapper movementMapper = new MovementMapper();

	private final LotMapper lotMapper = new LotMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new StockMovementController(movementMapper, lotMapper, movBrowserManagerMock,
				movStockInsertingManagerMock, medicalBrowsingManagerMock))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			// a standalone setup writes dates as arrays of numbers, while the running application writes
			// them as ISO strings; giving the converter the same object mapper means what is asserted
			// below is the response clients actually receive
			.setMessageConverters(new MappingJackson2HttpMessageConverter(MovementHelper.getObjectMapper()))
			.build();
		// the same mapper the application builds in ModelMapperConfig: the date and time types are
		// only handled once the JSR-310 module is registered, so a mapper without it would not
		// exercise the conversion this test is about
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		modelMapper.registerModule(new Jsr310Module());
		ReflectionTestUtils.setField(movementMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(lotMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetMovements_200() throws Exception {
		List<Movement> movements = MovementHelper.genList(2);
		LocalDateTime movementDate = movements.get(0).getDate();
		LocalDateTime dueDate = movements.get(0).getLot().getDueDate();

		when(movBrowserManagerMock.getMovements()).thenReturn(movements);

		this.mockMvc
			.perform(get("/stockmovements"))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(movements.size())))
			// the movement and its lot are recorded with a time of day: it has to survive the mapping,
			// which is what a date-only field on the DTO both truncated and made the mapper reject
			.andExpect(jsonPath("$[0].date").value(movementDate.toString()))
			.andExpect(jsonPath("$[0].lot.dueDate").value(dueDate.toString()));

		assertThat(movementDate.toLocalTime()).isNotEqualTo(LocalTime.MIDNIGHT);
	}
}
