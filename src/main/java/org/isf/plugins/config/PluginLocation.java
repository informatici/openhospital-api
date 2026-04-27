package org.isf.plugins.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PluginLocation {
	@JsonProperty("main") MAIN("main"), @JsonProperty("patient") PATIENT("patient");
	final String value;

	PluginLocation(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
