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
package org.isf.usergroups.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.isf.OpenHospitalApiApplication;
import org.isf.menu.TestPermission;
import org.isf.menu.TestUserGroup;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.usergroups.data.UserGroupHelper;
import org.isf.usergroups.dto.GroupPermissionsDTO;
import org.isf.usergroups.dto.UserGroupDTO;
import org.isf.usergroups.mapper.UserGroupMapper;
import org.isf.utils.exception.OHDataValidationException;
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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class UserGroupControllerTest {

	private final Logger LOGGER = LoggerFactory.getLogger(UserGroupControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserGroupMapper userGroupMapper;

	@Autowired
	private PermissionMapper permissionMapper;

	@MockBean
	private UserBrowsingManager userManager;

	@MockBean
	private PermissionManager permissionManager;

	@MockBean
	private GroupPermissionManager groupPermissionManager;

	@Test
	@WithMockUser(username = "admin", authorities = { "usergroups.read" })
	@DisplayName("Get all user groups")
	void getAllUserGroups() throws Exception {
		List<UserGroup> userGroups = List.of(UserGroupHelper.generateUserGroup());
		List<UserGroupDTO> userGroupDTOS = userGroups.stream().map(userGroup -> userGroupMapper.map2DTO(userGroup)).toList();

		when(userManager.getUserGroup()).thenReturn(userGroups);

		var result = mvc.perform(
										get("/usergroups").contentType(MediaType.APPLICATION_JSON))
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

		when(userManager.newUserGroup(any(), any())).thenReturn(userGroup);
		when(userManager.findUserGroupByCode(any())).thenReturn(userGroup);

		var result = mvc.perform(
										post("/usergroups").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userGroupDTO)))
						.andDo(log())
						.andExpect(status().isCreated())
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
										get("/usergroups/{group_code}", userGroup.getCode()).contentType(MediaType.APPLICATION_JSON))
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
										delete("/usergroups/{group_code}", userGroup.getCode()).contentType(MediaType.APPLICATION_JSON))
						.andDo(log())
						.andExpect(status().isNoContent())
						.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Nested
	@DisplayName("Update user group")
	class UpdateUserGroup {

		@Test
		@WithMockUser(username = "admin", authorities = { "usergroups.update" })
		@DisplayName("Should update user group")
		void shouldUpdateUserGroup() throws Exception {
			List<Permission> permissions = TestPermission.generatePermissions(4);
			UserGroup userGroup = UserGroupHelper.generateUserGroup();

			List<PermissionDTO> permissionDTOS = permissionMapper.map2DTOList(permissions);
			UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);
			userGroupDTO.setPermissions(permissionDTOS);

			when(userManager.findUserGroupByCode(any())).thenReturn(userGroup);
			when(userManager.updateUserGroup(any(), any())).thenReturn(true);

			var result = mvc.perform(
											put("/usergroups/{group_code}", userGroupDTO.getCode())
															.contentType(MediaType.APPLICATION_JSON)
															.content(objectMapper.writeValueAsString(userGroupDTO))
							)
							.andDo(log())
							.andExpect(status().isOk())
							.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "usergroups.update" })
		@DisplayName("Should fail to update user group when invalid payload")
		void shouldFailUpdateUserGroupWhenInvalidPayload() throws Exception {
			UserGroup userGroup = UserGroupHelper.generateUserGroup();

			UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);

			var result = mvc.perform(
											put("/usergroups/{group_code}", "DD")
															.contentType(MediaType.APPLICATION_JSON)
															.content(objectMapper.writeValueAsString(userGroupDTO))
							)
							.andDo(log())
							.andExpect(status().isBadRequest())
							.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin")
		@DisplayName("Should throw forbidden error when updating user group")
		void shouldThrowForbiddenErrorWhenUpdatingUserGroup() throws Exception {
			UserGroup userGroup = UserGroupHelper.generateUserGroup();

			UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);

			var result = mvc.perform(
											put("/usergroups/{group_code}", userGroupDTO.getCode())
															.contentType(MediaType.APPLICATION_JSON)
															.content(objectMapper.writeValueAsString(userGroupDTO))
							)
							.andDo(log())
							.andExpect(status().isForbidden())
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
											post("/usergroups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
															.contentType(MediaType.APPLICATION_JSON)
							)
							.andDo(log())
							.andExpect(status().isCreated())
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
											post("/usergroups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
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
											post("/usergroups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
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
											delete("/usergroups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
															.contentType(MediaType.APPLICATION_JSON)
							)
							.andDo(log())
							.andExpect(status().isNoContent())
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
											post("/usergroups/{group_code}/permissions/{id}", userGroup.getCode(), permission.getId())
															.contentType(MediaType.APPLICATION_JSON)
							)
							.andDo(log())
							.andExpect(status().is4xxClientError())
							.andExpect(content().string(containsString("not found")));
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "grouppermission.create", "grouppermission.delete" })
		@DisplayName("Should update user group permissions")
		void shouldUpdateUserGroupPermissions() throws Exception {
			UserGroup userGroup = new TestUserGroup().setup(false);
			Permission permission = TestPermission.generatePermission();

			when(userManager.findUserGroupByCode(any())).thenReturn(userGroup);
			when(groupPermissionManager.update(any(), any())).thenReturn(List.of(permission));

			var result = mvc.perform(
											put("/usergroups/{group_code}/permissions", userGroup.getCode())
															.content(objectMapper.writeValueAsString(new GroupPermissionsDTO(List.of(permission.getId()))))
															.contentType(MediaType.APPLICATION_JSON)
							)
							.andDo(log())
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.[0].id").value(permission.getId()))
							.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@WithMockUser(username = "admin", authorities = { "grouppermission.delete" })
		@DisplayName("Should fail to update user group permissions when insufficient permissions")
		void shouldFailToUpdateUserGroupPermissionsWhenInsufficientPermissions() throws Exception {

			var result = mvc.perform(
											put("/usergroups/{group_code}/permissions", "12")
															.content(objectMapper.writeValueAsString(new GroupPermissionsDTO(List.of(44))))
															.contentType(MediaType.APPLICATION_JSON)
							)
							.andDo(log())
							.andExpect(status().isForbidden())
							.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}
}
