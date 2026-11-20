/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.isf.OpenHospitalApiApplication;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.model.Permission;
import org.isf.users.data.UserHelper;
import org.isf.users.dto.UserDTO;
import org.isf.users.mapper.UserMapper;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserMapper userMapper;

	@MockitoBean
	private UserBrowsingManager userManager;

	@MockitoBean
	private PermissionManager permissionManager;

	@Test
	@WithMockUser(username = "admin", authorities = { "users.create" })
	void newUser_shouldReturnCreated() throws Exception {
		User user = UserHelper.generateUser();
		UserDTO userDTO = userMapper.map2DTO(user);

		when(userManager.isPasswordValid(any())).thenReturn(true);
		when(userManager.newUser(any())).thenReturn(user);

		mvc.perform(
				post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userDTO))
			)
			.andDo(log())
			.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "users.create" })
	void newUser_shouldIgnoreMustChangePasswordFlag_whenSentInPayload() throws Exception {
		User user = UserHelper.generateUser();
		UserDTO userDTO = userMapper.map2DTO(user);

		// the core layer is the one flagging every new user for a password change
		User createdUser = UserHelper.generateUser();
		createdUser.setPasswdMustChange(true);

		when(userManager.isPasswordValid(any())).thenReturn(true);
		when(userManager.newUser(any())).thenReturn(createdUser);

		// the extra property must be ignored: UserDTO intentionally doesn't expose the flag
		ObjectNode payload = objectMapper.valueToTree(userDTO);
		payload.put("passwdMustChange", true);

		mvc.perform(
				post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(payload.toString())
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString(user.getUserName())))
			.andExpect(content().string(not(containsString("passwdMustChange"))))
			.andExpect(jsonPath("$.passwd").doesNotExist());

		ArgumentCaptor<User> newUser = ArgumentCaptor.forClass(User.class);
		verify(userManager).newUser(newUser.capture());
		assertThat(newUser.getValue().isPasswdMustChange()).isFalse();
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "users.create" })
	void newUser_shouldReturnBadRequest_whenPasswordIsWeak() throws Exception {
		User user = UserHelper.generateUser();
		user.setPasswd("weak");
		UserDTO userDTO = userMapper.map2DTO(user);

		when(userManager.isPasswordValid(any())).thenReturn(false);

		mvc.perform(
				post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userDTO))
			)
			.andDo(log())
			.andExpect(status().isBadRequest());

		verify(userManager, never()).newUser(any());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "users.delete" })
	void deleteUser_shouldReturnNoContent() throws Exception {
		User user = UserHelper.generateUser();

		when(userManager.getUserByName(user.getUserName(), true)).thenReturn(user);
		doNothing().when(userManager).deleteUser(any());

		mvc.perform(
				delete("/users/{username}", user.getUserName()).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNoContent());
	}
	@Test
	@WithMockUser(username = "john")
	void retrieveProfileByCurrentLoggedInUser_shouldReturnOk() throws Exception {
		User user = UserHelper.generateUsers(1).get(0);
		user.setUserName("john");

		when(permissionManager.retrievePermissionsByUsername(user.getUserName())).thenReturn(Collections.emptyList());
		when(userManager.getUserByName(user.getUserName())).thenReturn(user);

		mvc.perform(
				get("/users/me").contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk());
	}

	@Nested
	@DisplayName("Get user")
	class GetUser {

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read" })
		void getUserByName_shouldReturnOk() throws Exception {
			User user = UserHelper.generateUsers(1).get(0);

			when(userManager.getUserByName(user.getUserName(), true)).thenReturn(user);

			mvc.perform(
					get("/users/{username}", user.getUserName()).contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isOk());
		}

		@Test
		@WithMockUser(username = "user")
		void getUserByName_shouldReturnForbidden_whenUserLacksReadAuthority() throws Exception {
			mvc.perform(
					get("/users/user").contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isForbidden());
		}

		@Test
		@WithMockUser(username = "user", authorities = { "users.read" })
		void getUserByName_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
			when(userManager.getUserByName(any())).thenReturn(null);

			mvc.perform(
					get("/users/user").contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("Get users")
	class GetUsers {

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read" })
		void getUser_shouldReturnAllUsers() throws Exception {
			List<User> users = UserHelper.generateUsers(3);
			List<UserDTO> userDTOS = userMapper.map2DTOList(users);

			when(userManager.getUser()).thenReturn(users);

			mvc.perform(
					get("/users").contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(UserHelper.asJsonString(userDTOS.stream().peek(user -> user.setPasswd(null)).toList()))));
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read" })
		void getUser_shouldReturnGroupUsers_whenGroupIdIsGiven() throws Exception {
			List<User> users = UserHelper.generateUsers(3);
			List<UserDTO> userDTOS = userMapper.map2DTOList(users);
			String groupCode = "contrib";

			when(userManager.getUser(any())).thenReturn(users);

			mvc.perform(
					get("/users").queryParam("group_id", groupCode).contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(UserHelper.asJsonString(userDTOS.stream().peek(user -> user.setPasswd(null)).toList()))));
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read" })
		void getUserByName_shouldReturnUserWithoutPassword() throws Exception {
			User user = UserHelper.generateUser();
			UserDTO userDTO = userMapper.map2DTO(user);
			String username = "admin";

			when(userManager.getUserByName(username, true)).thenReturn(user);

			userDTO.setPasswd(null);

			mvc.perform(
					get("/users/{username}", username).contentType(MediaType.APPLICATION_JSON))
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(UserHelper.asJsonString(userDTO))));
		}
	}

	@Nested
	@DisplayName("Update user")
	class UpdateUserTests {

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void updateUser_shouldReturnOk() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(user);
			when(userManager.updatePassword(any())).thenReturn(user);
			when(userManager.isUserNamePresent(user.getUserName())).thenReturn(true);

			mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk());
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void updateUser_shouldReturnBadRequest_whenPathUsernameDoesNotMatchPayload() throws Exception {
			var user = new User("laboratorist", new UserGroup("doctor", "Doctor group"), "", "Simple user");
			mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isBadRequest());
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "roles.read" })
		void updateUser_shouldReturnForbidden_whenUserLacksUpdateAuthority() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isForbidden());
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(null);

			mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isNotFound());
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void updateUser_shouldUpdateOnlyPasswordAndSetMustChangePasswordFlag_whenPasswordIsGiven() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "?..passwordAA", "Simple user");

			when(userManager.updateUser(any())).thenReturn(user);
			when(userManager.updatePassword(any())).thenReturn(user);
			when(userManager.isUserNamePresent(user.getUserName())).thenReturn(true);
			when(userManager.isPasswordValid(any())).thenReturn(true);

			mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk());

			// OP-896: a password reset by an administrator must be changed by the user at next login
			ArgumentCaptor<User> updatedUser = ArgumentCaptor.forClass(User.class);
			verify(userManager).updatePassword(updatedUser.capture());
			assertThat(updatedUser.getValue().isPasswdMustChange()).isTrue();
			verify(userManager, never()).updateUser(any());
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void updateUser_shouldReturnBadRequest_whenPasswordIsWeak() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "weak", "Simple user");

			when(userManager.isUserNamePresent(user.getUserName())).thenReturn(true);
			when(userManager.isPasswordValid(any())).thenReturn(false);

			mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isBadRequest());

			verify(userManager, never()).updatePassword(any());
			verify(userManager, never()).updateUser(any());
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "users.read", "users.update" })
		void updateUser_shouldUpdateFieldsButNotPassword_whenPasswordIsEmpty() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(user);
			when(userManager.updatePassword(any())).thenReturn(user);
			when(userManager.isUserNamePresent(user.getUserName())).thenReturn(true);

			mvc.perform(
					put("/users/doctor").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk());

			verify(userManager, never()).updatePassword(any());
			verify(userManager).updateUser(any());
		}
	}

	@Nested
	@DisplayName("Update profile")
	class UpdateProfileTests {

		@BeforeEach
		void setup() throws OHServiceException {
			var permission = new Permission();
			permission.setName("users.read");
			permission.setDescription("Allow to read users");
			when(permissionManager.retrievePermissionsByUsername(any())).thenReturn(List.of(permission));
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldReturnOk() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(user);
			when(userManager.updatePassword(any())).thenReturn(user);
			when(userManager.getUserByName(any())).thenReturn(user);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldReturnForbidden_whenProfileBelongsToAnotherUser() throws Exception {
			var user = new User("laboratorist", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(user);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isForbidden());
		}

		@Test
		void updateProfile_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");
			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isUnauthorized());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldUpdateOnlyPassword_whenPasswordIsGiven() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "?..passwordAA", "Simple user");

			when(userManager.updateUser(any())).thenReturn(user);
			when(userManager.updatePassword(any())).thenReturn(user);
			when(userManager.getUserByName(any())).thenReturn(user);
			when(userManager.isPasswordValid(any())).thenReturn(true);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk());

			verify(userManager).updatePassword(any());
			verify(userManager, never()).updateUser(any());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldReturnForbidden_whenPasswordMustChangeAndNewPasswordIsEmpty() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");
			user.setPasswdMustChange(true);

			when(userManager.getUserByName(any())).thenReturn(user);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isForbidden())
				.andExpect(content().string(containsString("The password must be changed before updating the profile.")));

			verify(userManager, never()).updateUser(any());
			verify(userManager, never()).updatePassword(any());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldReturnForbidden_whenPasswordIsExpiredAndNewPasswordIsEmpty() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(user);
			when(userManager.isPasswordExpired(any())).thenReturn(true);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isForbidden());

			verify(userManager, never()).updateUser(any());
			verify(userManager, never()).updatePassword(any());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldUpdatePasswordAndClearMustChangePasswordFlag_whenPasswordMustChange() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "?..passwordAA", "Simple user");
			user.setPasswdMustChange(true);

			when(userManager.getUserByName(any())).thenReturn(user);
			when(userManager.updatePassword(any())).thenReturn(user);
			when(userManager.isPasswordValid(any())).thenReturn(true);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isOk());

			ArgumentCaptor<User> updatedUser = ArgumentCaptor.forClass(User.class);
			verify(userManager).updatePassword(updatedUser.capture());
			assertThat(updatedUser.getValue().isPasswdMustChange()).isFalse();
			verify(userManager, never()).updateUser(any());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldReturnBadRequest_whenPasswordIsWeak() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "weak", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(user);
			when(userManager.isPasswordValid(any())).thenReturn(false);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isBadRequest());

			verify(userManager, never()).updatePassword(any());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldReturnBadRequest_whenPasswordIsWeakAndSameAsCurrent() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "SamePass1@", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(user);
			when(userManager.isSameAsCurrentPassword(any(), any())).thenReturn(true);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isBadRequest());

			verify(userManager, never()).updatePassword(any());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldReturnBadRequest_whenPasswordIsSameAsCurrent() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "SamePass1@", "Simple user");

			when(userManager.getUserByName(any())).thenReturn(user);
			when(userManager.isPasswordValid(any())).thenReturn(true);
			when(userManager.isSameAsCurrentPassword(any(), any())).thenReturn(true);

			mvc.perform(
					put("/users/me").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userMapper.map2DTO(user))))
				.andDo(log())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("The new password must be different from the current one.")));

			verify(userManager, never()).updatePassword(any());
		}

		@Test
		@WithMockUser(username = "doctor")
		void updateProfile_shouldUpdateFieldsButNotPassword_whenPasswordIsEmpty() throws Exception {
			var user = new User("doctor", new UserGroup("doctor", "Doctor group"), "", "Simple user");

			when(userManager.updateUser(any())).thenReturn(user);
			when(userManager.updatePassword(any())).thenReturn(user);
			when(userManager.getUserByName(any())).thenReturn(user);

			mvc.perform(
					put("/users/me")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userMapper.map2DTO(user)))
						.with(user("doctor").roles("exams.read"))
				)
				.andDo(log())
				.andExpect(status().isOk());

			verify(userManager, never()).updatePassword(any());
			verify(userManager).updateUser(any());
		}
	}
}
