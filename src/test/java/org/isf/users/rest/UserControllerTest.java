package org.isf.users.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.OpenHospitalApiApplication;
import org.isf.OpenHospitalApiApplicationTests;
import org.isf.lab.rest.LaboratoryControllerTest;
import org.isf.menu.dto.UserDTO;
import org.isf.menu.dto.UserGroupDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.rest.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = OpenHospitalApiApplication.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

	@Nested
	@DisplayName("Update user")
	class UpdateUserTests {

		private final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

		@Autowired
		private MockMvc mvc;

		@Autowired
		private ObjectMapper objectMapper;

		@Autowired
		private UserMapper userMapper;

		@SpyBean
		private UserBrowsingManager userManager;

		@Test
		@DisplayName("Should update the user")
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void shouldUpdateTheUser() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(put("/users/doctor").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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
			var result = mvc.perform(put("/users/doctor").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isBadRequest())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update user when insufficient permissions")
		@WithMockUser(username = "admin", authorities = { "users.read" })
		void shouldFailToUpdateUserWhenInsufficientPermissions() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");
			var result = mvc.perform(put("/users/doctor").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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

			var result = mvc.perform(put("/users/doctor").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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

			var result = mvc.perform(put("/users/doctor").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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

			var result = mvc.perform(put("/users/doctor").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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

		private final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

		@Autowired
		private MockMvc mvc;

		@Autowired
		private ObjectMapper objectMapper;

		@Autowired
		private UserMapper userMapper;

		@SpyBean
		private UserBrowsingManager userManager;

		@Test
		@DisplayName("Should update user profile")
		@WithMockUser(username = "doctor")
		void shouldUpdateUserProfile() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(true);
			when(userManager.updatePassword(any())).thenReturn(true);
			when(userManager.getUserByName(any())).thenReturn(user);

			var result = mvc.perform(put("/users").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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

			var result = mvc.perform(put("/users").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isForbidden())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update when not authenticated")
		void shouldFailToUpdateUserWhenNotAuthenticated() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");
			var result = mvc.perform(put("/users").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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

			var result = mvc.perform(put("/users").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
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

			var result = mvc.perform(put("/users").content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk())
				.andReturn();

			LOGGER.debug("result: {}", result);

			verify(userManager, never()).updatePassword(any());
			verify(userManager).updateUser(any());
		}
	}
}
