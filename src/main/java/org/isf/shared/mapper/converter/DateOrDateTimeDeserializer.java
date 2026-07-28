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
package org.isf.shared.mapper.converter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Reads a date and time, accepting a plain date as well.
 * <p>
 * Fields that carry a date and a time are written as {@code 2020-06-24T10:00:00}, and that is what
 * clients are expected to send. Some of them have been sending {@code 2020-06-24} instead, from the
 * days when the field was declared as a date only, and rejecting those requests would break callers
 * that work today for no gain: a date with no time means the start of that day, which is exactly
 * what the field held before. Anything else is left to fail as usual, so a genuinely malformed value
 * is still reported.
 */
public class DateOrDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		String value = parser.getText();
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return LocalDateTime.parse(value.trim());
		} catch (DateTimeParseException dateTimeNotThere) {
			return LocalDate.parse(value.trim()).atStartOfDay();
		}
	}
}
