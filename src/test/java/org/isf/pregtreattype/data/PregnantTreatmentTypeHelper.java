package org.isf.pregtreattype.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.disctype.data.DischargeTypeHelper;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.test.TestDischargeType;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.test.TestPregnantTreatmentType;
import org.isf.utils.exception.OHException;

public class PregnantTreatmentTypeHelper {
	
	public static PregnantTreatmentType setup() throws OHException {
		TestPregnantTreatmentType testPregnantTreatmentType = new TestPregnantTreatmentType();
		return testPregnantTreatmentType.setup(false);
	}

	public static ArrayList<PregnantTreatmentType> setupPregnantTreatmentTypeList(int size) {
		return (ArrayList<PregnantTreatmentType>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return PregnantTreatmentTypeHelper.setup();
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}

}
