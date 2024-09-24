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

import org.isf.OpenHospitalApiApplication;
import org.isf.menu.data.UserHelper;
import org.isf.menu.dto.UserDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.model.User;

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

	@MockBean
	private UserBrowsingManager userManager;

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
}
