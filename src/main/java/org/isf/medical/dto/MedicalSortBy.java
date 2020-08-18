package org.isf.medical.dto;

public enum MedicalSortBy {
	NONE("none"), CODE("code"), NAME("name");
	
	String value;
	
	MedicalSortBy(String value) {
		this.value = value;
	}
}
