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
package org.isf.users.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import org.isf.OpenHospitalApiApplication;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.users.data.UserHelper;
import org.isf.users.dto.UserDTO;
import org.isf.users.mapper.UserMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.model.Permission;
import org.isf.utils.exception.OHServiceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

	@MockBean
	private UserBrowsingManager userManager;

	@MockBean
	private PermissionManager permissionManager;

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

	@Nested
	@DisplayName("Update user")
	class UpdateUserTests {

		@Test
		@DisplayName("Should update the user")
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void shouldUpdateTheUser() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update when username in the patch doesn't match")
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void shouldFailToUpdateWhenUsernameInThePathDoesNtMatch() throws Exception {
			var user = new User("laboratorist", new UserGroup("doctor", "Doctor group"), "", "Simple user");
			var result = mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isBadRequest())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update user when insufficient permissions")
		@WithMockUser(username = "admin", authorities = { "roles.read" })
		void shouldFailToUpdateUserWhenInsufficientPermissions() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			var result = mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isForbidden())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail when user to update doesn't exist")
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void shouldFailWhenUserToUpdateIsNotFound() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(null);

			var result = mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isNotFound())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should update only password")
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void shouldUpdateOnlyPassword() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "?..passwordAA", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk())
				.andReturn();

			LOGGER.debug("result: {}", result);

			verify(userManager).updatePassword(any());
			verify(userManager, never()).updateUser(any());
		}

		@Test
		@DisplayName("Should update user without updating password")
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void shouldUpdateUserWithoutUpdatingPassword() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk())
				.andReturn();

			LOGGER.debug("result: {}", result);

			verify(userManager, never()).updatePassword(any());
			verify(userManager).updateUser(any());
		}
	}

	@Nested
	@DisplayName("Update profile")
	class UpdateProfileTests {

		@BeforeEach
		public void setup() throws OHServiceException {
			var permission = new Permission();
			permission.setName("users.read");
			permission.setDescription("Allow to read users");
			when(permissionManager.retrievePermissionsByUsername(any())).thenReturn(List.of(permission));
		}

		@Test
		@DisplayName("Should update user profile")
		@WithMockUser(username = "doctor")
		void shouldUpdateUserProfile() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail when trying to update another user's profile")
		@WithMockUser(username = "doctor")
		void shouldFailWhenTryingToUpdateAnotherUsersProfile() throws Exception {
			var user = new User("laboratorist", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isForbidden())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update when not authenticated")
		void shouldFailToUpdateUserWhenNotAuthenticated() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");
			var result = mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isUnauthorized())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should update only password")
		@WithMockUser(username = "doctor")
		void shouldUpdateOnlyPassword() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "?..passwordAA", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk())
				.andReturn();

			LOGGER.debug("result: {}", result);

			verify(userManager).updatePassword(any());
			verify(userManager, never()).updateUser(any());
		}

		@Test
		@DisplayName("Should update user without updating password")
		@WithMockUser(username = "doctor")
		void shouldUpdateUserWithoutUpdatingPassword() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(
					put("/users/me")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userMapper.map2DTO(user)))
						.with(user("doctor").roles("exams.read"))
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andReturn();

			LOGGER.debug("result: {}", result);

			verify(userManager, never()).updatePassword(any());
			verify(userManager).updateUser(any());
		}
	}
}
