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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpSession;

import org.isf.OpenHospitalApiApplication;
import org.isf.login.dto.LoginRequest;
import org.isf.login.dto.LoginResponse;
import org.isf.login.dto.PasswordPolicyDTO;
import org.isf.login.dto.TokenRefreshRequest;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.security.CustomAuthenticationManager;
import org.isf.security.jwt.TokenProvider;
import org.isf.security.jwt.TokenValidationResult;
import org.isf.sessionaudit.manager.SessionAuditManager;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.users.data.UserHelper;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.jsonwebtoken.JwtException;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
class LoginControllerTest {

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

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);

		LoginController loginController = new LoginController(
			httpSession, sessionAuditManager, tokenProvider, authenticationManager, userManager
		);

		this.mvc = MockMvcBuilders
			.standaloneSetup(loginController)
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testAuthenticateUser_Success() throws Exception {
		String username = "testUser";
		String password = "testPassword";
		String mockToken = "mockJwtToken";
		String mockRefreshToken = "mockRefreshToken";

		// Create a mock User object
		User user = new User();
		user.setUserName(username);
		user.setPasswd(password);

		// Create a mock Authentication object
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);

		LoginRequest loginRequest = new LoginRequest(username, password);

		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(tokenProvider.generateJwtToken(any(), eq(false), eq(false))).thenReturn(mockToken);
		when(tokenProvider.generateRefreshToken(any())).thenReturn(mockRefreshToken);
		when(userManager.getUserByName(username)).thenReturn(user);

		// Expected LoginResponse object
		LoginResponse loginResponse = new LoginResponse(mockToken, mockRefreshToken, username);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		// Perform the login request
		mvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(UserHelper.asJsonString(loginRequest))))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
	}

	@Test
	void testGetPasswordPolicy() throws Exception {
		when(userManager.isStrongPasswordEnabled()).thenReturn(true);
		when(userManager.getPasswordMinLength()).thenReturn(6);
		when(userManager.getPasswordStrengthRegex()).thenReturn("^(?=.*[0-9]).+$");

		PasswordPolicyDTO expected = new PasswordPolicyDTO(true, 6, "^(?=.*[0-9]).+$");
		String expectedJson = UserHelper.asJsonString(expected);

		mvc.perform(get("/auth/password-policy"))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
	}

	@Test
	void testAuthenticateUser_MustChangePassword() throws Exception {
		String username = "testUser";
		String password = "testPassword";
		String mockToken = "mockJwtToken";
		String mockRefreshToken = "mockRefreshToken";

		// A user flagged to change the password at next login (OP-896)
		User user = new User();
		user.setUserName(username);
		user.setPasswd(password);
		user.setPasswdMustChange(true);

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);

		LoginRequest loginRequest = new LoginRequest(username, password);

		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(tokenProvider.generateJwtToken(any(), eq(false), eq(true))).thenReturn(mockToken);
		when(tokenProvider.generateRefreshToken(any())).thenReturn(mockRefreshToken);
		when(userManager.getUserByName(username)).thenReturn(user);
		when(userManager.getPasswordLeaseDays()).thenReturn(90);

		// Expected LoginResponse with mustChangePassword = true but passwordExpired = false: the change was forced by an administrator, not by the lease
		LoginResponse loginResponse = new LoginResponse(mockToken, mockRefreshToken, username, true, false, 90);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		mvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(UserHelper.asJsonString(loginRequest))))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
	}

	@Test
	void testAuthenticateUser_PasswordLeaseExpired() throws Exception {
		String username = "testUser";
		String password = "testPassword";
		String mockToken = "mockJwtToken";
		String mockRefreshToken = "mockRefreshToken";

		// The flag is off, but the password lease has expired (OP-896)
		User user = new User();
		user.setUserName(username);
		user.setPasswd(password);
		user.setPasswdMustChange(false);

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);

		LoginRequest loginRequest = new LoginRequest(username, password);

		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(tokenProvider.generateJwtToken(any(), eq(false), eq(true))).thenReturn(mockToken);
		when(tokenProvider.generateRefreshToken(any())).thenReturn(mockRefreshToken);
		when(userManager.getUserByName(username)).thenReturn(user);
		when(userManager.isPasswordExpired(user)).thenReturn(true);
		when(userManager.getPasswordLeaseDays()).thenReturn(90);

		// Expected LoginResponse with mustChangePassword = true driven by the expired lease: passwordExpired = true and the configured lease is reported
		LoginResponse loginResponse = new LoginResponse(mockToken, mockRefreshToken, username, true, true, 90);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		mvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(UserHelper.asJsonString(loginRequest))))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
	}

	// TODO testAuthenticateUser_Failure

	@Test
	void testRefreshToken_Success() throws Exception {
		String refreshToken = "validRefreshToken";
		String newAccessToken = "newAccessToken";
		String username = "testUser";
		String newRefreshToken = "newValidRefreshToken";

		// Create a mock TokenRefreshRequest object
		TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

		// A user that does not need to change the password
		User user = new User();
		user.setUserName(username);

		when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
		when(tokenProvider.validateToken(refreshToken)).thenReturn(TokenValidationResult.VALID);
		when(tokenProvider.getAuthenticationByUsername(username)).thenReturn(mock(Authentication.class));
		when(tokenProvider.generateJwtToken(any(), eq(false), eq(false))).thenReturn(newAccessToken);
		when(tokenProvider.generateRefreshToken(any())).thenReturn(newRefreshToken);
		when(userManager.getUserByName(username)).thenReturn(user);

		// Expected LoginResponse object
		LoginResponse loginResponse = new LoginResponse(newAccessToken, newRefreshToken, username);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		// Perform POST request to refresh-token endpoint
		mvc.perform(post("/auth/refresh-token")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(UserHelper.asJsonString(request))))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
	}

	@Test
	void testRefreshToken_MustChangePassword() throws Exception {
		String refreshToken = "validRefreshToken";
		String newAccessToken = "newAccessToken";
		String username = "testUser";
		String newRefreshToken = "newValidRefreshToken";

		TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

		// A user still flagged to change the password: the flag must be recomputed, not silently cleared (OP-896)
		User user = new User();
		user.setUserName(username);
		user.setPasswdMustChange(true);

		when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
		when(tokenProvider.validateToken(refreshToken)).thenReturn(TokenValidationResult.VALID);
		when(tokenProvider.getAuthenticationByUsername(username)).thenReturn(mock(Authentication.class));
		when(tokenProvider.generateJwtToken(any(), eq(false), eq(true))).thenReturn(newAccessToken);
		when(tokenProvider.generateRefreshToken(any())).thenReturn(newRefreshToken);
		when(userManager.getUserByName(username)).thenReturn(user);

		// Expected LoginResponse with mustChangePassword = true
		LoginResponse loginResponse = new LoginResponse(newAccessToken, newRefreshToken, username, true);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		mvc.perform(post("/auth/refresh-token")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(UserHelper.asJsonString(request))))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
	}

	@Test
	void testRefreshToken_PasswordLeaseExpired() throws Exception {
		String refreshToken = "validRefreshToken";
		String newAccessToken = "newAccessToken";
		String username = "testUser";
		String newRefreshToken = "newValidRefreshToken";

		TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

		// The flag is off, but the password lease has expired: the refresh must recompute the reason as well (OP-896)
		User user = new User();
		user.setUserName(username);
		user.setPasswdMustChange(false);

		when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
		when(tokenProvider.validateToken(refreshToken)).thenReturn(TokenValidationResult.VALID);
		when(tokenProvider.getAuthenticationByUsername(username)).thenReturn(mock(Authentication.class));
		when(tokenProvider.generateJwtToken(any(), eq(false), eq(true))).thenReturn(newAccessToken);
		when(tokenProvider.generateRefreshToken(any())).thenReturn(newRefreshToken);
		when(userManager.getUserByName(username)).thenReturn(user);
		when(userManager.isPasswordExpired(user)).thenReturn(true);
		when(userManager.getPasswordLeaseDays()).thenReturn(90);

		// Expected LoginResponse with mustChangePassword = true driven by the expired lease: passwordExpired = true and the configured lease is reported
		LoginResponse loginResponse = new LoginResponse(newAccessToken, newRefreshToken, username, true, true, 90);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		mvc.perform(post("/auth/refresh-token")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(UserHelper.asJsonString(request))))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
	}

	@Test
	void testRefreshToken_FlagCheckFails_FailsClosed() throws Exception {
		String refreshToken = "validRefreshToken";
		String newAccessToken = "newAccessToken";
		String username = "testUser";
		String newRefreshToken = "newValidRefreshToken";

		TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

		// The database check fails: the flag must fail closed instead of silently dropping the restriction (OP-896)
		when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
		when(tokenProvider.validateToken(refreshToken)).thenReturn(TokenValidationResult.VALID);
		when(tokenProvider.getAuthenticationByUsername(username)).thenReturn(mock(Authentication.class));
		when(tokenProvider.generateJwtToken(any(), eq(false), eq(true))).thenReturn(newAccessToken);
		when(tokenProvider.generateRefreshToken(any())).thenReturn(newRefreshToken);
		when(userManager.getUserByName(username)).thenThrow(new OHServiceException(new OHExceptionMessage("Database error")));
		when(userManager.getPasswordLeaseDays()).thenReturn(90);

		// Expected LoginResponse with mustChangePassword = true but no reason: passwordExpired stays false and no lease is reported
		// even though the lease policy is active, so the client falls back to a generic message
		LoginResponse loginResponse = new LoginResponse(newAccessToken, newRefreshToken, username, true, false, null);
		String expectedJson = UserHelper.asJsonString(loginResponse);

		mvc.perform(post("/auth/refresh-token")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(UserHelper.asJsonString(request))))
			.andExpect(status().isOk())
			.andExpect(content().string(Objects.requireNonNull(expectedJson)))
			.andReturn();
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
					.content(Objects.requireNonNull(UserHelper.asJsonString(request))))
			.andExpect(status().isBadRequest())
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
					.content(Objects.requireNonNull(UserHelper.asJsonString(request))))
			.andExpect(content().string(containsString("Refresh token expired or invalid")));
	}

}
