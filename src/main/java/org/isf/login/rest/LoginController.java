/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.login.rest;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.isf.login.dto.LoginRequest;
import org.isf.login.dto.LoginResponse;
import org.isf.security.UserDetailsServiceImpl;
import org.isf.security.jwt.TokenProvider;
import org.isf.sessionaudit.manager.SessionAuditManager;
import org.isf.sessionaudit.model.SessionAudit;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.ErrorDescription;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private SessionAuditManager sessionAuditManager;
	
	@Autowired
	private TokenProvider tokenProvider;

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LoginController.class);
	
	 /**
     * Implemented by Spring Security
     */
    @ApiOperation(value = "Login", notes = "Login with the given credentials.")
    @ApiResponses({@ApiResponse(code = 200, message = "", response = LoginResponse.class)})
    @PostMapping(value = "/auth/login")
    void login(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    ) {
        throw new IllegalStateException("Add Spring Security to handle authentication");
    }

//    /**
//     * Implemented by Spring Security
//     */
//    @ApiOperation(value = "Logout", notes = "Logout the current user.")
//    @ApiResponses({@ApiResponse(code = 200, message = "")})
//    @PostMapping(value = "/auth/logout")
//    void logout() {
//        throw new IllegalStateException("Add Spring Security to handle authentication");
//    }

//	@PostMapping(value = "/auth/login", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity< ? > authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws OHAPIException {
//		if (loginRequest.getPassword().length() < 10) {
//			throw new OHAPIException(new OHExceptionMessage(null, ErrorDescription.PASSWORD_TOO_SHORT, "password too short", OHSeverityLevel.ERROR));
//		}
//		Authentication authentication = authenticationManager
//						.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//		String jwt = tokenProvider.generateJwtToken(authentication, true);
//
//		User userDetails = (User) authentication.getPrincipal();
//
//
//		try {
//			this.httpSession.setAttribute("sessionAuditId",
//							sessionAuditManager.newSessionAudit(new SessionAudit(authentication.getName(), LocalDateTime.now(), null)));
//		} catch (OHServiceException e1) {
//			LOGGER.error("Unable to log user login in the session_audit table");
//		}
//
//		return ResponseEntity.ok(new LoginResponse(jwt, userDetails.getUsername()));
//	}
}
