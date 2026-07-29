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
package org.isf.medicalstock.data;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.TestMovement;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class MovementHelper {

	/**
	 * A time of day other than midnight, seconds included, so that a movement built here tells apart a
	 * date carrying its time from one that has silently lost it.
	 */
	private static final LocalTime TIME_OF_DAY = LocalTime.of(14, 30, 15);

	private static ObjectMapper objectMapper;

	public static Movement setup() throws OHException {
		MedicalType medicalType = new TestMedicalType().setup(false);
		Medical medical = new TestMedical().setup(medicalType, false);
		MovementType movementType = new TestMovementType().setup(false);
		Ward ward = new TestWard().setup(false);
		Lot lot = new TestLot().setup(medical, false);
		lot.setPreparationDate(lot.getPreparationDate().with(TIME_OF_DAY));
		lot.setDueDate(lot.getDueDate().with(TIME_OF_DAY));
		Supplier supplier = new TestSupplier().setup(false);
		Movement movement = new TestMovement().setup(medical, movementType, ward, lot, supplier, false);
		movement.setDate(movement.getDate().with(TIME_OF_DAY));
		return movement;
	}

	public static Movement setup(int id) throws OHException {
		Movement movement = setup();
		movement.setCode(id);
		return movement;
	}

	public static List<Movement> genList(int n) {
		return IntStream.range(0, n)
			.mapToObj(i -> {
				try {
					return setup(i);
				} catch (OHException e) {
					throw new IllegalStateException(e);
				}
			}).toList();
	}

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper()
				.registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				// as the application does: dates on the wire are ISO strings, not arrays of numbers
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		}
		return objectMapper;
	}
}
