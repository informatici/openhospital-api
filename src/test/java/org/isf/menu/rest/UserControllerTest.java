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
import org.isf.menu.TestPermission;
import org.isf.menu.TestUserGroup;
import org.isf.menu.data.UserGroupHelper;
import org.isf.menu.data.UserHelper;
import org.isf.menu.data.UserSettingHelper;
import org.isf.menu.dto.UserDTO;
import org.isf.menu.dto.UserGroupDTO;
import org.isf.menu.dto.UserSettingDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.mapper.UserGroupMapper;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserSettingMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserSetting;
import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
	private UserGroupMapper userGroupMapper;

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
	@WithMockUser(username = "admin", authorities = { "usergroups.read" })
	@DisplayName("Get all user groups")
	void getAllUserGroups() throws Exception {
		List<UserGroup> userGroups = List.of(UserGroupHelper.generateUserGroup());
		List<UserGroupDTO> userGroupDTOS = userGroups.stream().map(userGroup -> userGroupMapper.map2DTO(userGroup)).toList();

		when(userManager.getUserGroup()).thenReturn(userGroups);

		var result = mvc.perform(
				get("/users/groups").contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(UserGroupHelper.asJsonString(userGroupDTOS))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "usergroups.create" })
	@DisplayName("Create user group")
	void createUserGroup() throws Exception {
		List<Permission> permissions = TestPermission.generatePermissions(4);
		UserGroup userGroup = UserGroupHelper.generateUserGroup();

		List<PermissionDTO> permissionDTOS = permissionMapper.map2DTOList(permissions);
		UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);
		userGroupDTO.setPermissions(permissionDTOS);

		when(userManager.newUserGroup(userGroup, permissions)).thenReturn(userGroup);

		var result = mvc.perform(
				post("/users/groups").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userGroupDTO)))
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString("true")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "usergroups.update" })
	@DisplayName("Update user group")
	void updateUserGroup() throws Exception {
		List<Permission> permissions = TestPermission.generatePermissions(4);
		UserGroup userGroup = UserGroupHelper.generateUserGroup();

		List<PermissionDTO> permissionDTOS = permissionMapper.map2DTOList(permissions);
		UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);
		userGroupDTO.setPermissions(permissionDTOS);

		when(userManager.findUserGroupByCode(any())).thenReturn(userGroup);
		when(userManager.updateUserGroup(any(), any())).thenReturn(true);

		var result = mvc.perform(
				put("/users/groups")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userGroupDTO))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("true")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "usergroups.read" })
	@DisplayName("Get user group")
	void getUserGroup() throws Exception {
		List<Permission> permissions = TestPermission.generatePermissions(4);
		UserGroup userGroup = UserGroupHelper.generateUserGroup();

		List<GroupPermission> groupPermissions = permissions.stream().map(permission -> {
			GroupPermission groupPermission = new GroupPermission();
			groupPermission.setUserGroup(userGroup);
			groupPermission.setPermission(permission);

			return groupPermission;
		}).toList();

		List<PermissionDTO> permissionDTOS = permissionMapper.map2DTOList(permissions);
		UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);
		userGroupDTO.setPermissions(permissionDTOS);

		when(userManager.findUserGroupByCode(userGroup.getCode())).thenReturn(userGroup);
		when(groupPermissionManager.findUserGroupPermissions(any())).thenReturn(groupPermissions);

		var result = mvc.perform(
				get("/users/groups/{group_code}", userGroup.getCode()).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(userGroupDTO))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "usergroups.delete" })
	@DisplayName("Delete user groups")
	void deleteUserGroup() throws Exception {
		UserGroup userGroup = UserGroupHelper.generateUserGroup();
		UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);

		when(userManager.getUserGroup()).thenReturn(List.of(userGroup));
		doNothing().when(userManager).deleteGroup(any());

		var result = mvc.perform(
				delete("/users/groups/{group_code}", userGroup.getCode()).contentType(MediaType.APPLICATION_JSON))
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

	@Nested
	@DisplayName("Assign / revoke permission")
	class UserGroupPermissions {

		@Test
		@WithMockUser(username = "admin", authorities = { "usergroups.create", "grouppermission.create" })
		@DisplayName("Assign a permission to a user group")
		void assignPermissionToUserGroup() throws Exception {
			UserGroup userGroup = new TestUserGroup().setup(false);
			Permission permission = TestPermission.generatePermission();
			GroupPermission groupPermission = new GroupPermission();
			groupPermission.setUserGroup(userGroup);
			groupPermission.setPermission(permission);

			when(userManager.findUserGroupByCode(any())).thenReturn(userGroup);
			when(permissionManager.retrievePermissionById(anyInt())).thenReturn(permission);
			when(groupPermissionManager.create(any(), any())).thenReturn(groupPermission);

			var result = mvc.perform(
					post("/users/groups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isCreated())
				.andExpect(content().string(containsString("true")))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "usergroups.create", "grouppermission.create" })
		@DisplayName("Assign already assigned permission to a user group")
		void assignAlreadyAssignedPermissionToUserGroup() throws Exception {
			UserGroup userGroup = new TestUserGroup().setup(false);
			Permission permission = TestPermission.generatePermission();
			GroupPermission groupPermission = new GroupPermission();
			groupPermission.setUserGroup(userGroup);
			groupPermission.setPermission(permission);

			when(userManager.findUserGroupByCode(any())).thenReturn(userGroup);
			when(permissionManager.retrievePermissionById(anyInt())).thenReturn(permission);
			when(groupPermissionManager.create(any(), any())).thenThrow(
				new OHDataValidationException(new OHExceptionMessage(""))
			);

			mvc.perform(
					post("/users/groups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(content().string(containsString("Failed")));
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "usergroups.create", "grouppermission.create" })
		@DisplayName("Assign non existing permission to a user group")
		void assignNonExistingPermissionToUserGroup() throws Exception {
			UserGroup userGroup = new TestUserGroup().setup(false);
			Permission permission = TestPermission.generatePermission();
			GroupPermission groupPermission = new GroupPermission();
			groupPermission.setUserGroup(userGroup);
			groupPermission.setPermission(permission);

			when(userManager.findUserGroupByCode(any())).thenReturn(null);
			when(permissionManager.retrievePermissionById(anyInt())).thenReturn(null);
			when(groupPermissionManager.create(any(), any())).thenReturn(groupPermission);

			mvc.perform(
					post("/users/groups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(content().string(containsString("not found")));
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "grouppermission.delete" })
		@DisplayName("Revoke a permission from a user group")
		void revokePermissionFromUserGroup() throws Exception {
			UserGroup userGroup = new TestUserGroup().setup(false);
			Permission permission = TestPermission.generatePermission();
			GroupPermission groupPermission = new GroupPermission();
			groupPermission.setUserGroup(userGroup);
			groupPermission.setPermission(permission);

			when(userManager.findUserGroupByCode(any())).thenReturn(userGroup);
			when(permissionManager.retrievePermissionById(anyInt())).thenReturn(permission);
			doNothing().when(groupPermissionManager).delete(any(), any());

			var result = mvc.perform(
					delete("/users/groups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("true")))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "grouppermission.create" })
		@DisplayName("Revoke not assigned permission to a user group")
		void revokeNotAssignedPermissionToUserGroup() throws Exception {
			UserGroup userGroup = new TestUserGroup().setup(false);
			Permission permission = TestPermission.generatePermission();
			GroupPermission groupPermission = new GroupPermission();
			groupPermission.setUserGroup(userGroup);
			groupPermission.setPermission(permission);

			when(userManager.findUserGroupByCode(any())).thenReturn(null);
			when(permissionManager.retrievePermissionById(anyInt())).thenReturn(null);
			when(groupPermissionManager.create(any(), any())).thenReturn(groupPermission);

			mvc.perform(
					post("/users/groups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(content().string(containsString("not found")));
		}
	}
}
