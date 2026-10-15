/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.stats.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describes a stat report that can be launched by the user.
 */
public class ReportLauncherDTO {

	@Schema(description = "Folder holding the compiled report", example = "rpt_stat", accessMode = Schema.AccessMode.READ_ONLY)
	private String folder;

	@Schema(description = "Name of the report file (without extension)", example = "POI_ByAgeBySex", accessMode = Schema.AccessMode.READ_ONLY)
	private String fileName;

	@Schema(description = "Localized title of the report", example = "Patients by age and sex", accessMode = Schema.AccessMode.READ_ONLY)
	private String title;

	@Schema(description = "Names of the parameters the user is expected to provide", example = "[\"month\", \"year\"]",
					accessMode = Schema.AccessMode.READ_ONLY)
	private List<String> userInputParameters;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getUserInputParameters() {
		return userInputParameters;
	}

	public void setUserInputParameters(List<String> userInputParameters) {
		this.userInputParameters = userInputParameters;
	}
}
