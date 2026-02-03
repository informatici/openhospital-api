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
package org.isf.menu.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.servlet.ServletContext;

import org.springframework.stereotype.Service;

@Service
public class VersionService {

	private final ServletContext servletContext;

	public VersionService(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getVersion() throws IOException {
		// Trying when using Spring Boot
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.isf/openhospital-api/pom.properties");

		// Trying when deployed as WAR
		if (inputStream == null && servletContext != null) {
			inputStream = servletContext.getResourceAsStream("/META-INF/maven/org.isf/openhospital-api/pom.properties");
		}

		if (inputStream == null) {
			throw new IOException("pom.properties not found in classpath or servlet context");
		}

		Properties properties = new Properties();
		properties.load(inputStream);

		return properties.getProperty("version");
	}
}