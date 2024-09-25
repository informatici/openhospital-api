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
package org.isf.usersettings.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.isf.OpenHospitalApiApplication;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.model.User;
import org.isf.menu.model.UserSetting;
import org.isf.users.data.UserHelper;
import org.isf.usersettings.data.UserSettingHelper;
import org.isf.usersettings.dto.UserSettingDTO;
import org.isf.usersettings.mapper.UserSettingMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class UserSettingControllerTest {

	private final Logger LOGGER = LoggerFactory.getLogger(UserSettingControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserSettingMapper userSettingMapper;

	@MockBean
	private UserBrowsingManager userManager;

	@MockBean
	private UserSettingManager userSettingManager;

	@Test
	@WithMockUser(username = "contrib")
	@DisplayName("Get logged-in user's setting")
	void getCurrentUserSettings() throws Exception {
		List<UserSetting> userSettings = UserSettingHelper.generateMany(5);
		List<UserSettingDTO> userSettingDTOS = userSettingMapper.map2DTOList(userSettings);

		when(userSettingManager.getUserSettingByUserName(any())).thenReturn(userSettings);

		var result = mvc.perform(
										get("/usersettings").contentType(MediaType.APPLICATION_JSON))
						.andDo(log())
						.andExpect(status().isOk())
						.andExpect(content().string(containsString(UserSettingHelper.asJsonString(userSettingDTOS))))
						.andReturn();

		LOGGER.debug("result: {}", result);
	}
	@Test
	@WithMockUser(username = "admin")
	@DisplayName("Should get user setting by setting name")
	void shouldGetUserSettingBySettingName() throws Exception {
		UserSetting userSetting = UserSettingHelper.generate();
		userSetting.setId(1);
		userSetting.setUser("admin");
		UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

		when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(userSetting);

		var result = mvc.perform(
										get("/usersettings/{configName}", userSetting.getConfigName())
														.contentType(MediaType.APPLICATION_JSON)
														.content(objectMapper.writeValueAsString(userSettingDTO))
						)
						.andDo(log())
						.andExpect(status().isOk())
						.andExpect(content().string(containsString(objectMapper.writeValueAsString(userSettingDTO))))
						.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Nested
	@DisplayName("Create / update user setting")
	class SaveUserSettings {

		@Test
		@WithMockUser(username = "oh user")
		@DisplayName("Should create own user setting")
		void shouldCreateOwnUserSetting() throws Exception {
			User user = UserHelper.generateUser();
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(null);
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.newUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
											post("/usersettings")
															.contentType(MediaType.APPLICATION_JSON)
															.content(objectMapper.writeValueAsString(userSettingDTO))
							)
							.andDo(log())
							.andExpect(status().isCreated())
							.andExpect(content().string(containsString(objectMapper.writeValueAsString(userSettingDTO))))
							.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "oh user")
		@DisplayName("Should fail to create user setting for another user")
		void shouldFailToCreateUserSettingForOtherUser() throws Exception {
			User user = UserHelper.generateUser();
			user.setUserName("other user");
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(null);
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.newUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
											post("/usersettings")
															.contentType(MediaType.APPLICATION_JSON)
															.content(objectMapper.writeValueAsString(userSettingDTO))
							)
							.andDo(log())
							.andExpect(status().is4xxClientError())
							.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "oh user")
		@DisplayName("Should update own user setting")
		void shouldUpdateOwnUserSetting() throws Exception {
			User user = UserHelper.generateUser();
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			userSetting.setId(1);
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.updateUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
											put("/usersettings/{id}", userSetting.getId())
															.contentType(MediaType.APPLICATION_JSON)
															.content(objectMapper.writeValueAsString(userSettingDTO))
							)
							.andDo(log())
							.andExpect(status().isOk())
							.andExpect(content().string(containsString(objectMapper.writeValueAsString(userSettingDTO))))
							.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "oh user")
		@DisplayName("Should fail to update user setting for another user")
		void shouldFailToUpdateUserSettingForOtherUser() throws Exception {
			User user = UserHelper.generateUser();
			user.setUserName("other user");
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			userSetting.setId(1);
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.updateUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
											put("/usersettings/{id}", userSetting.getId())
															.contentType(MediaType.APPLICATION_JSON)
															.content(objectMapper.writeValueAsString(userSettingDTO))
							)
							.andDo(log())
							.andExpect(status().isForbidden())
							.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}

	@Nested
	@DisplayName("Delete user settings")
	class DeleteUserSettings {

		@Test
		@WithMockUser(username = "contrib")
		@DisplayName("Should delete own user setting by id")
		void shouldDeleteOwnUserSettingById() throws Exception {
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser("contrib");
			userSetting.setId(1);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			doNothing().when(userSettingManager).deleteUserSetting(any());

			var result = mvc.perform(
											delete("/usersettings/{id}", userSetting.getId())
															.contentType(MediaType.APPLICATION_JSON)
							)
							.andDo(log())
							.andExpect(status().isOk())
							.andExpect(content().string(containsString("true")))
							.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "oh user")
		@DisplayName("Should fail to delete user setting for another user")
		void shouldFailToDeleteUserSettingForOtherUser() throws Exception {
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser("other-user");
			userSetting.setId(1);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			doNothing().when(userSettingManager).deleteUserSetting(any());

			var result = mvc.perform(
											delete("/usersettings/{id}", userSetting.getId())
															.contentType(MediaType.APPLICATION_JSON)
							)
							.andDo(log())
							.andExpect(status().is4xxClientError())
							.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}
}
