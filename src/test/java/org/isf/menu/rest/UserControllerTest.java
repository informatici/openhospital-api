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
package org.isf.menu.rest;

import java.util.List;
import java.util.Optional;

import org.isf.OpenHospitalApiApplication;
import org.isf.menu.data.UserHelper;
import org.isf.menu.data.UserSettingHelper;
import org.isf.menu.dto.UserDTO;
import org.isf.menu.dto.UserSettingDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserSettingMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserSetting;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	private final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserSettingMapper userSettingMapper;

	@Autowired
	private PermissionMapper permissionMapper;

	@MockBean
	private UserBrowsingManager userManager;

	@MockBean
	private PermissionManager permissionManager;

	@MockBean
	private UserSettingManager userSettingManager;

	@MockBean
	private GroupPermissionManager groupPermissionManager;

	@Nested
	@DisplayName("Get users")
	class GetUsers {

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read" })
		@DisplayName("Get all users")
		void getAllUsers() throws Exception {
			List<User> users = UserHelper.generateUsers(3);
			List<UserDTO> userDTOS = userMapper.map2DTOList(users);

			when(userManager.getUser()).thenReturn(users);

			var result = mvc.perform(
					get("/users").contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(UserHelper.asJsonString(userDTOS.stream().peek(user -> user.setPasswd(null)).toList()))))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read" })
		@DisplayName("Get users by group code")
		void getAllUsersByGroupCode() throws Exception {
			List<User> users = UserHelper.generateUsers(3);
			List<UserDTO> userDTOS = userMapper.map2DTOList(users);
			String groupCode = "contrib";

			when(userManager.getUser(any())).thenReturn(users);

			var result = mvc.perform(
					get("/users").queryParam("group_id", groupCode).contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(UserHelper.asJsonString(userDTOS.stream().peek(user -> user.setPasswd(null)).toList()))))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read" })
		@DisplayName("Get user by name")
		void getUserByName() throws Exception {
			User user = UserHelper.generateUser();
			UserDTO userDTO = userMapper.map2DTO(user);
			String username = "admin";

			when(userManager.getUserByName(any())).thenReturn(user);

			userDTO.setPasswd(null);

			var result = mvc.perform(
					get("/users/{username}", username).contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(UserHelper.asJsonString(userDTO))))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "users.create" })
	@DisplayName("Create user")
	void createUser() throws Exception {
		User user = UserHelper.generateUser();
		UserDTO userDTO = userMapper.map2DTO(user);

		when(userManager.newUser(any())).thenReturn(user);

		var result = mvc.perform(
				post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userDTO))
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString("true")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "users.delete" })
	@DisplayName("Delete user")
	void deleteUser() throws Exception {
		User user = UserHelper.generateUser();

		when(userManager.getUserByName(any())).thenReturn(user);
		doNothing().when(userManager).deleteUser(any());

		var result = mvc.perform(
				delete("/users/{username}", user.getUserName()).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("true")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "contrib", authorities = { "users.read", "usersettings.read" })
	@DisplayName("Get logged-in user's setting")
	void getCurrentUserSettings() throws Exception {
		List<UserSetting> userSettings = UserSettingHelper.generateMany(5);
		List<UserSettingDTO> userSettingDTOS = userSettingMapper.map2DTOList(userSettings);

		when(userSettingManager.getUserSettingByUserName(any())).thenReturn(userSettings);

		var result = mvc.perform(
				get("/users/settings").contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(UserSettingHelper.asJsonString(userSettingDTOS))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Nested
	@DisplayName("Create / update user setting")
	class SaveUserSettings {

		@Test
		@WithMockUser(username = "admin", authorities = { "users.create", "usersettings.create" })
		@DisplayName("Create own user setting as admin")
		void createOwnUserSettingAsAdmin() throws Exception {
			User user = UserHelper.generateUser();
			user.setUserName("admin");
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(null);
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.newUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
					post("/users/settings")
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
		@WithMockUser(username = "oh user", authorities = { "users.create", "usersettings.create" })
		@DisplayName("Create own user setting as non-admin user")
		void createOwnUserSettingAsNonAdmin() throws Exception {
			User user = UserHelper.generateUser();
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(null);
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.newUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
					post("/users/settings")
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
		@WithMockUser(username = "admin", authorities = { "users.create", "usersettings.create" })
		@DisplayName("Should fail to create user setting for another user")
		void shouldFailToCreateUserSettingForOtherUser() throws Exception {
			User user = UserHelper.generateUser();
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(null);
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.newUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
					post("/users/settings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userSettingDTO))
				)
				.andDo(log())
				.andExpect(status().isForbidden())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "oh user", authorities = { "users.create", "usersettings.create" })
		@DisplayName("Create user setting for another user as non-admin user")
		void createUserSettingForOtherUserAsNonAdmin() throws Exception {
			User user = UserHelper.generateUser();
			user.setUserName("other user");
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(null);
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.newUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
					post("/users/settings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userSettingDTO))
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.update", "usersettings.update" })
		@DisplayName("Update own user setting as admin")
		void updateOwnUserSettingAsAdmin() throws Exception {
			User user = UserHelper.generateUser();
			user.setUserName("admin");
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			userSetting.setId(1);
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.updateUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
					put("/users/settings/{id}", userSetting.getId())
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
		@WithMockUser(username = "oh user", authorities = { "users.update", "usersettings.update" })
		@DisplayName("Update own user setting as non-admin user")
		void updateOwnUserSettingAsNonAdmin() throws Exception {
			User user = UserHelper.generateUser();
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser(user.getUserName());
			userSetting.setId(1);
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.updateUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
					put("/users/settings/{id}", userSetting.getId())
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
		@WithMockUser(username = "admin", authorities = { "users.update", "usersettings.update" })
		@DisplayName("Update user setting as admin for another user")
		void updateUserSettingAsAdminForOtherUser() throws Exception {
			User user = UserHelper.generateUser();
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser("admin");
			userSetting.setId(1);
			UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			when(userManager.getUserByName(userSetting.getUser())).thenReturn(user);
			when(userSettingManager.updateUserSetting(any())).thenReturn(userSetting);

			var result = mvc.perform(
					put("/users/settings/{id}", userSetting.getId())
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
		@WithMockUser(username = "oh user", authorities = { "users.update", "usersettings.update" })
		@DisplayName("Should fail to update user setting for another user as non-admin user")
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
					put("/users/settings/{id}", userSetting.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userSettingDTO))
				)
				.andDo(log())
				.andExpect(status().isForbidden())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "users.read", "usersettings.read" })
	@DisplayName("Get user setting by id")
	void getUserSettingById() throws Exception {
		UserSetting userSetting = UserSettingHelper.generate();
		userSetting.setId(1);
		userSetting.setUser("admin");
		UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

		when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));

		var result = mvc.perform(
				get("/users/settings/{id}", userSetting.getId())
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
	@WithMockUser(username = "admin", authorities = { "users.read", "usersettings.read" })
	@DisplayName("Get user setting by username and setting name")
	void getUserSettingBySettingName() throws Exception {
		UserSetting userSetting = UserSettingHelper.generate();
		userSetting.setId(1);
		UserSettingDTO userSettingDTO = userSettingMapper.map2DTO(userSetting);

		when(userSettingManager.getUserSettingByUserName(any())).thenReturn(List.of(userSetting));
		when(userSettingManager.getUserSettingByUserNameConfigName(any(), any())).thenReturn(userSetting);

		var result = mvc.perform(
				get("/users/{userName}/settings/{configName}", userSetting.getUser(), userSetting.getConfigName())
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(userSettingDTO))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Nested
	@DisplayName("Delete user settings")
	class DeleteUserSettings {

		@Test
		@WithMockUser(username = "admin", authorities = { "users.delete", "usersettings.delete" })
		@DisplayName("Delete own user setting by id as admin")
		void deleteOwnUserSettingByIdAsAdmin() throws Exception {
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser("admin");
			userSetting.setId(1);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			doNothing().when(userSettingManager).deleteUserSetting(any());

			var result = mvc.perform(
					delete("/users/settings/{id}", userSetting.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("true")))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "contrib", authorities = { "users.delete", "usersettings.delete" })
		@DisplayName("Delete own user setting by id as non-admin")
		void deleteOwnUserSettingByIdAsNonAdmin() throws Exception {
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser("contrib");
			userSetting.setId(1);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			doNothing().when(userSettingManager).deleteUserSetting(any());

			var result = mvc.perform(
					delete("/users/settings/{id}", userSetting.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("true")))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.delete", "usersettings.delete" })
		@DisplayName("Delete user setting for another user by id as admin")
		void deleteUserSettingByIdAsAdmin() throws Exception {
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setId(1);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			doNothing().when(userSettingManager).deleteUserSetting(any());

			var result = mvc.perform(
					delete("/users/settings/{id}", userSetting.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("true")))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "oh user", authorities = { "users.update", "usersettings.update" })
		@DisplayName("Delete user setting for another user as non-admin user")
		void deleteUserSettingForOtherUserAsNonAdmin() throws Exception {
			UserSetting userSetting = UserSettingHelper.generate();
			userSetting.setUser("other-user");
			userSetting.setId(1);

			when(userSettingManager.getUserSettingById(anyInt())).thenReturn(Optional.of(userSetting));
			doNothing().when(userSettingManager).deleteUserSetting(any());

			var result = mvc.perform(
					delete("/users/settings/{id}", userSetting.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}
}
