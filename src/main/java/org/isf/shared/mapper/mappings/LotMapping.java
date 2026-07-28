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
package org.isf.shared.mapper.mappings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.isf.medicalstock.dto.LotDTO;
import org.isf.medicalstock.model.Lot;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class LotMapping {

	/**
	 * A lot carries its dates as {@link LocalDateTime} in the model, but the API exposes them as
	 * plain dates: the time of day is never chosen by the user, it is only the start or the end of
	 * the day picked in the date field. ModelMapper has no conversion between the two types and
	 * fails the whole mapping when it meets one, so both directions are declared here.
	 * <p>
	 * Incoming dates are widened the way the GUI does it in {@code GoodDateChooser}: the preparation
	 * date to the start of its day, the due date to the last second of its day, so that a lot is not
	 * treated as expired throughout the day it expires on.
	 */
	public static void addMapping(ModelMapper modelMapper) {

		Converter<LocalDateTime, LocalDate> toLocalDate =
			context -> context.getSource() == null ? null : context.getSource().toLocalDate();
		Converter<LocalDate, LocalDateTime> toStartOfDay =
			context -> context.getSource() == null ? null : context.getSource().atStartOfDay().truncatedTo(ChronoUnit.SECONDS);
		Converter<LocalDate, LocalDateTime> toEndOfDay =
			context -> context.getSource() == null ? null : context.getSource().atTime(LocalTime.MAX).truncatedTo(ChronoUnit.SECONDS);

		modelMapper.typeMap(Lot.class, LotDTO.class).addMappings(mapper -> {
			mapper.using(toLocalDate).map(Lot::getPreparationDate, LotDTO::setPreparationDate);
			mapper.using(toLocalDate).map(Lot::getDueDate, LotDTO::setDueDate);
		});

		modelMapper.typeMap(LotDTO.class, Lot.class).addMappings(mapper -> {
			mapper.using(toStartOfDay).map(LotDTO::getPreparationDate, Lot::setPreparationDate);
			mapper.using(toEndOfDay).map(LotDTO::getDueDate, Lot::setDueDate);
		});
	}
}
