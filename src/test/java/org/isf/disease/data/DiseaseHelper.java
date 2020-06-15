package org.isf.disease.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.disease.model.Disease;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.test.TestDiseaseType;
import org.isf.utils.exception.OHException;

public class DiseaseHelper {

	public static Disease setup() throws OHException {
		TestDiseaseType testDiseaseType = new  TestDiseaseType();
		DiseaseType diseaseType = testDiseaseType.setup(false); 
		TestDisease td = new TestDisease();
		return td.setup(diseaseType, false);
	}
	
	public static Disease setup(DiseaseType diseaseType) throws OHException {
		TestDisease td = new TestDisease();
		return td.setup(diseaseType, false);
	}
	
	public static ArrayList<Disease> setupDiseaseList(int size) {
		return (ArrayList<Disease>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return DiseaseHelper.setup();
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}

	

}
