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
		TestOperationType testOperationType = new  TestOperationType();
		OperationType operationType = testOperationType.setup(false); 
		return OperationHelper.setup(operationType);
	}
	
	public static Operation setup(OperationType operationType) throws OHException {
		TestOperation testOperation = new TestOperation();
		return testOperation.setup(operationType, false);
	}

	public static ArrayList<Operation> setupOperationList(int size) {
		return (ArrayList<Operation>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return OperationHelper.setup();
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}

}
