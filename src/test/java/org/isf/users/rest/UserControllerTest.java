package org.isf.users.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserGroupManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.mapper.UserGroupMapper;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserSettingMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.rest.UserController;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.LitePermissionMapper;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.rest.VaccineController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class UserControllerTest {

	private final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

	private MockMvc mvc;

	final private ObjectMapper objectMapper = new ObjectMapper()
		.registerModule(new ParameterNamesModule())
		.registerModule(new Jdk8Module())
		.registerModule(new JavaTimeModule());
	;

	final private UserMapper userMapper = new UserMapper();

	final private LitePermissionMapper litePermissionMapper = new LitePermissionMapper();

	final private UserGroupMapper userGroupMapper = new UserGroupMapper();

	final private UserSettingMapper userSettingMapper = new UserSettingMapper();

	@Mock
	private UserBrowsingManager userManager;

	@Mock
	private PermissionManager permissionManager;

	@Mock
	private UserSettingManager userSettingManager;

	@Mock
	private UserGroupManager userGroupManager;

	private AutoCloseable closeable;

	@BeforeEach
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mvc = MockMvcBuilders
			.standaloneSetup(
				new UserController(permissionManager, litePermissionMapper, userMapper, userGroupMapper, userManager, userSettingManager, userSettingMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		List.of(userMapper, litePermissionMapper, userGroupMapper, userSettingMapper).forEach((item) -> {
			ReflectionTestUtils.setField(item, "modelMapper", modelMapper);
		});
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
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
