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
package org.isf.medicalstock.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.isf.medicalstockward.dto.MovementWardDTO;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The stock movement dates carry a time, and used to be declared as plain dates.
 * <p>
 * Callers that were written against the old declaration send a date with no time to the charge,
 * discharge and ward movement endpoints. Those requests have to keep working: they are the only
 * part of this surface that ever did, since every endpoint reading these DTOs answered 500.
 */
class MovementDateCompatibilityTest {

	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	@Test
	void aRequestSendingAPlainDateIsStillAccepted() throws Exception {
		MovementDTO movement = objectMapper.readValue("{\"code\":1,\"date\":\"2020-06-24\"}", MovementDTO.class);
		LotDTO lot = objectMapper.readValue(
			"{\"code\":\"LOT1\",\"preparationDate\":\"2020-06-24\",\"dueDate\":\"2021-06-24\"}", LotDTO.class);
		MovementWardDTO wardMovement = objectMapper.readValue("{\"code\":1,\"date\":\"2020-06-24\"}", MovementWardDTO.class);

		assertThat(movement.getDate()).isEqualTo(LocalDateTime.of(2020, 6, 24, 0, 0));
		assertThat(lot.getPreparationDate()).isEqualTo(LocalDateTime.of(2020, 6, 24, 0, 0));
		assertThat(lot.getDueDate()).isEqualTo(LocalDateTime.of(2021, 6, 24, 0, 0));
		assertThat(wardMovement.getDate()).isEqualTo(LocalDateTime.of(2020, 6, 24, 0, 0));
	}

	@Test
	void aRequestSendingADateAndTimeKeepsIt() throws Exception {
		MovementDTO movement = objectMapper.readValue("{\"code\":1,\"date\":\"2020-06-24T10:15:30\"}", MovementDTO.class);

		assertThat(movement.getDate()).isEqualTo(LocalDateTime.of(2020, 6, 24, 10, 15, 30));
	}

	@Test
	void aValueThatIsNeitherIsStillRejected() {
		assertThatThrownBy(() -> objectMapper.readValue("{\"code\":1,\"date\":\"not a date\"}", MovementDTO.class))
			.isInstanceOf(Exception.class);
	}
}
