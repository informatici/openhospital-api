/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.operation.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.operation.model.Operation;
import org.isf.operation.test.TestOperation;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.test.TestOperationType;
import org.isf.utils.exception.OHException;

public class OperationHelper {

	public static Operation setup() throws OHException {
		TestOperationType testOperationType = new TestOperationType();
		OperationType operationType = testOperationType.setup(false);
		return OperationHelper.setup(operationType);
	}

	public static Operation setup(OperationType operationType) throws OHException {
		TestOperation testOperation = new TestOperation();
		return testOperation.setup(operationType, false);
	}

	public static ArrayList<Operation> setupOperationList(int size) {
		return (ArrayList<Operation>) IntStream.range(0, size)
				.mapToObj(i -> {
					try {
						return OperationHelper.setup();
					} catch (OHException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
	}

}
