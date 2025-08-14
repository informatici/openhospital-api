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
package org.isf.settings.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.OpenHospitalApiApplication;
import org.isf.settings.data.SettingHelper;
import org.isf.settings.dto.UpdateSettingDTO;
import org.isf.settings.manager.SettingManager;
import org.isf.settings.mapper.SettingMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SettingController integration tests
 * @author Silevester D.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class SettingControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SettingMapper mapper;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private SettingManager manager;

	@Test
	@WithMockUser(username = "admin", authorities = {"settings.read"})
	@DisplayName("Successfully retrieve setting by id")
	void testGetSettingByIdWithPrivilege() throws Exception {
		var setting = SettingHelper.boolSetting();
		var settingDTO = mapper.map2DTO(setting);

		when(manager.getById(anyInt())).thenReturn(setting);

		mvc.perform(get("/settings/{id}", 2)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(settingDTO))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "anonymous", authorities = {"privilege.read"})
	@DisplayName("Should return 403 when trying to get setting by id without privilege")
	void testGetSettingByIdWithoutPrivilege() throws Exception {
		mvc.perform(get("/settings/{id}", 2)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isForbidden())
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"settings.read"})
	@DisplayName("Successfully retrieve setting by code")
	void testGetSettingByCodeWithPrivilege() throws Exception {
		var setting = SettingHelper.boolSetting();
		var settingDTO = mapper.map2DTO(setting);

		when(manager.getByCode(anyString())).thenReturn(setting);

		mvc.perform(get("/settings/code/{code}", "CODE")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(settingDTO))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "anonymous", authorities = {"privilege.read"})
	@DisplayName("Should return 403 when trying to get setting by code without privilege")
	void testGetSettingByICodeWithoutPrivilege() throws Exception {
		mvc.perform(get("/settings/code/{code}", "CODE")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isForbidden())
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"settings.read"})
	@DisplayName("Successfully retrieve all settings")
	void testGetAllSettingsWithPrivilege() throws Exception {
		var settings = SettingHelper.generate(20);
		var settingDTOS = mapper.map2DTOList(settings);

		when(manager.findAll()).thenReturn(settings);

		mvc.perform(get("/settings")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(settingDTOS))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "anonymous", authorities = {"privilege.read"})
	@DisplayName("Should return 403 when trying to get settings without privilege")
	void testGetAllSettingsWithoutPrivilege() throws Exception {
		mvc.perform(get("/settings")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isForbidden())
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"settings.update"})
	@DisplayName("Successfully update setting")
	void testUpdateSettingWithPrivilege() throws Exception {
		var setting = SettingHelper.boolSetting();
		var dto = new UpdateSettingDTO();
		dto.setValue("FALSE");

		when(manager.getByCode(anyString())).thenReturn(setting);
		when(manager.update(any())).thenReturn(setting);

		setting.setValue(dto.getValue());
		var settingDTO = mapper.map2DTO(setting);

		mvc.perform(put("/settings/{code}", "CODE")
				.content(objectMapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(settingDTO))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "anonymous", authorities = {"privilege.read"})
	@DisplayName("Should return 403 when trying to update setting without privilege")
	void testUpdateSettingWithoutPrivilege() throws Exception {
		var dto = new UpdateSettingDTO();
		dto.setValue("FALSE");

		mvc.perform(put("/settings/{code}", "CODE")
				.content(objectMapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isForbidden())
			.andReturn();
	}
}
