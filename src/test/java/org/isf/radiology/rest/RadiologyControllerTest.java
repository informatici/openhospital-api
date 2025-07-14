/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

package org.isf.radiology.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.OpenHospitalApiApplication;
import org.isf.orthanc.model.Instance;
import org.isf.orthanc.model.InstanceResponse;
import org.isf.orthanc.model.Patient;
import org.isf.orthanc.model.Series;
import org.isf.orthanc.model.SeriesResponse;
import org.isf.orthanc.model.Study;
import org.isf.orthanc.model.StudyResponse;
import org.isf.orthanc.service.OrthancAPIClientService;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Radiology Controller Test
 * @author Silevester D.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class RadiologyControllerTest {
	private final Logger LOGGER = LoggerFactory.getLogger(RadiologyControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@MockBean
	private OrthancAPIClientService orthancAPIClientService;

	@MockBean
	private PatientBrowserManager patientBrowserManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser(username = "admin", authorities = {"radiology.read"})
	@DisplayName("Should successfully get a patient studies")
	void testGetPatientStudiesById() throws Exception {
		List<StudyResponse> studies = generateStudies();

		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(PatientHelper.setup());
		when(orthancAPIClientService.getPatientStudiesById(anyString())).thenReturn(studies);

		var result = mvc.perform(get("/radiology/patients/{id}/studies", "123")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(studies))))
			.andReturn();

		LOGGER.debug("Get patient's studies result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"radiology.read"})
	@DisplayName("Should successfully get study's series")
	void testGetStudySeriesById() throws Exception {
		List<SeriesResponse> series = generateSeries();

		when(orthancAPIClientService.getStudySeries(anyString())).thenReturn(series);

		var result = mvc.perform(get("/radiology/studies/{id}/series", "12-3223ndi32o")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(series))))
			.andReturn();

		LOGGER.debug("Get study's series result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"radiology.read"})
	@DisplayName("Should successfully get series' instances")
	void testGetSeriesInstancesById() throws Exception {
		List<InstanceResponse> instances = generateInstances();

		when(orthancAPIClientService.getSeriesInstances(anyString())).thenReturn(instances);

		var result = mvc.perform(get("/radiology/series/{id}/instances", "92932nd820e-23ej2d")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(instances))))
			.andReturn();

		LOGGER.debug("Get series' instances result: {}", result);
	}

	private List<StudyResponse> generateStudies() {
		return IntStream.range(0, 3).mapToObj(i -> {
			StudyResponse studyResponse = new StudyResponse();

			studyResponse.setParentPatientId("h93932he3280rj2d02ed" + i);
			studyResponse.setSeriesIds(List.of("9032483hj2d9ed" + i, "832u8032hd02edu92" + i));
			studyResponse.setId("29343029jd0dj20d2e" + i);
			studyResponse.setLabels(List.of("Test" + i));

			Patient patient = new Patient();
			patient.setId("90329432490-pat-" + i);
			patient.setName("Patient " + i);
			patient.setSex("F");
			patient.setBirthDate("1998121" + i);

			studyResponse.setPatient(patient);

			Study study = new Study();
			study.setId("jd382dh202jed2" + i);
			study.setDescription("20241015");
			study.setTime("10223"+ i);

			studyResponse.setStudy(study);
			return studyResponse;
		}).collect(Collectors.toList());
	}

	private List<SeriesResponse> generateSeries() {
		return IntStream.range(0, 2).mapToObj(i -> {
			SeriesResponse seriesResponse = new SeriesResponse();

			seriesResponse.setParentStudyId("h93932he3280lk3r4j2d02ed" + i);
			seriesResponse.setInstancesIds(List.of("903248kls33hj2d9ed" + i, "832u80kls3ds32hd02edu92" + i));
			seriesResponse.setId("29343029jd0dj20d2e" + i);
			seriesResponse.setLabels(List.of("Test" + i));
			seriesResponse.setExpectedNumberOfInstances("2");
			seriesResponse.setStatus("stable");

			Series series = new Series();
			series.setNumber("" + i);
			series.setSeriesDescription("R.A.S");
			series.setManufacturer("Manufacturer "+ i);

			seriesResponse.setSeries(series);
			return seriesResponse;
		}).collect(Collectors.toList());
	}

	private List<InstanceResponse> generateInstances() {
		return IntStream.range(0, 2).mapToObj(i -> {
			InstanceResponse instanceResponse = new InstanceResponse();

			instanceResponse.setId("29343029jd03dj3ue382d20d2e" + i);
			instanceResponse.setLabels(List.of("Test" + i));
			instanceResponse.setFileSize(2354234);
			instanceResponse.setFileUuid("0-32923493242ed" + i);
			instanceResponse.setIndexInSeries(i);

			Instance instance = new Instance();
			instance.setInstanceNumber("" + i);
			instance.setInstanceUID("29343029jd03dj3ue382d20d2e" + i);
			instance.setAcquisitionNumber("0103" + i);

			instanceResponse.setInstance(instance);

			return instanceResponse;
		}).collect(Collectors.toList());
	}
}
