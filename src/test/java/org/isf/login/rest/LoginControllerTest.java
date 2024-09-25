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
package org.isf.login.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.isf.OpenHospitalApiApplication;
import org.isf.login.dto.LoginRequest;
import org.isf.login.dto.LoginResponse;
import org.isf.login.dto.TokenRefreshRequest;
import org.isf.menu.data.UserHelper;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.menu.rest.UserControllerTest;
import org.isf.security.CustomAuthenticationManager;
import org.isf.security.UserDetailsServiceImpl;
import org.isf.security.jwt.TokenProvider;
import org.isf.security.jwt.TokenValidationResult;
import org.isf.sessionaudit.manager.SessionAuditManager;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.jsonwebtoken.JwtException;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
public class LoginControllerTest {

	private final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

	private MockMvc mvc;

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private HttpSession httpSession;

	@Mock
	private SessionAuditManager sessionAuditManager;

	@Mock
	private CustomAuthenticationManager authenticationManager;

	@Mock
	private UserBrowsingManager userManager;

	@MockBean
	private UserDetailsServiceImpl customUserDetailsService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		LoginController loginController = new LoginController(httpSession, sessionAuditManager, tokenProvider, authenticationManager, userManager);

		this.mvc = MockMvcBuilders
						.standaloneSetup(loginController)
						.setControllerAdvice(new OHResponseEntityExceptionHandler())
						.build();
	}

	@Test
	void testAuthenticateUser_Success() throws Exception {
		String username = "testUser";
		String password = "testPassword";

		// Create a mock User object
		User user = new User();
		user.setUserName(username);
		user.setPasswd(password);

		// Create a mock Authentication object
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);

		LoginRequest loginRequest = new LoginRequest(username, password);

		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(tokenProvider.generateJwtToken(any(), eq(false))).thenReturn("mockJwtToken");
		when(tokenProvider.generateRefreshToken(any())).thenReturn("mockRefreshToken");
		when(userManager.getUserByName(username)).thenReturn(user);

		// Perform the login request
		MvcResult result = mvc.perform(
						post("/auth/login")
										.contentType(MediaType.APPLICATION_JSON)
										.content(UserHelper.asJsonString(loginRequest)))
						.andReturn();

		// Get the actual JSON response
		String actualJson = result.getResponse().getContentAsString();
		System.out.println("Actual response JSON: " + actualJson); // Log the actual response

		// Update the expected JSON structure
		String expectedJson = "{\"token\":\"mockJwtToken\",\"refreshToken\":\"mockRefreshToken\",\"username\":\"" + username + "\"}";

		// Assert that the actual JSON matches the expected JSON
		JSONAssert.assertEquals(expectedJson, actualJson, false);

	}

	@Test
	void testRefreshToken_Success() throws Exception {
		String refreshToken = "validRefreshToken";
		String newAccessToken = "newAccessToken";
		String username = "testUser";

		TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

		when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
		when(tokenProvider.validateToken(refreshToken)).thenReturn(TokenValidationResult.VALID);
		when(tokenProvider.getAuthenticationByUsername(username)).thenReturn(mock(Authentication.class));
		when(tokenProvider.generateJwtToken(any(), eq(false))).thenReturn(newAccessToken);

		// Expected LoginResponse object (you need to define this)
		LoginResponse loginResponse = new LoginResponse(newAccessToken, refreshToken, username);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		// Perform POST request to refresh-token endpoint
		var result = mvc.perform(
						post("/auth/refresh-token")
										.accept(MediaType.APPLICATION_JSON)
										.contentType(MediaType.APPLICATION_JSON)
										.content(UserHelper.asJsonString(request))) // Serialize request to JSON
						.andExpect(status().isOk())
						.andExpect(content().string(expectedJson))
						.andReturn();

		LOGGER.debug("result: {}", result.getResponse().getContentAsString());
		LOGGER.debug("result: {}", result);
	}

	@Test
	void testRefreshToken_Invalid() throws Exception {
		String invalidRefreshToken = "invalidRefreshToken";

		TokenRefreshRequest request = new TokenRefreshRequest(invalidRefreshToken);

		// Mock the TokenProvider to return INVALID when validating the refresh token
		when(tokenProvider.validateToken(invalidRefreshToken)).thenReturn(TokenValidationResult.INVALID_SIGNATURE);

		// Perform POST request to refresh-token endpoint
		mvc.perform(
						post("/auth/refresh-token")
										.contentType(MediaType.APPLICATION_JSON)
										.accept(MediaType.APPLICATION_JSON)
										.content(UserHelper.asJsonString(request))) // Serialize request to JSON
						.andExpect(status().isBadRequest()) // Expect 400 Bad Request
						.andExpect(content().string(containsString("Invalid Refresh Token")));
	}

	@Test
	void testRefreshToken_JwtException() throws Exception {
		String expiredRefreshToken = "expiredRefreshToken";

		TokenRefreshRequest request = new TokenRefreshRequest(expiredRefreshToken);

		// Mock the TokenProvider to throw a JwtException when trying to validate the refresh token
		when(tokenProvider.validateToken(expiredRefreshToken)).thenThrow(new JwtException("Token expired or invalid"));

		// Perform POST request to refresh-token endpoint
		mvc.perform(
						post("/auth/refresh-token")
										.contentType(MediaType.APPLICATION_JSON)
										.accept(MediaType.APPLICATION_JSON)
										.content(UserHelper.asJsonString(request))) // Serialize request to JSON
						.andExpect(status().isUnauthorized()) // Expect 401 Unauthorized
						.andExpect(content().string(containsString("Refresh token expired or invalid")));
	}

}
