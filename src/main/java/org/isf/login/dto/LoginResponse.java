/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Class representing a Login response")
public class LoginResponse {

	@Schema(description = "Token")
	private String token;

	@Schema(description = "RefreshToken")
	private String refreshToken;

	@Schema(description = "Type of Token", example = "Bearer")
	private String type = "Bearer";

	@Schema(description = "User name", example = "admin")
	private String username;

	@Schema(description = "Whether the user must change the password before using the application")
	private boolean mustChangePassword;

	public LoginResponse() {
	}

	public LoginResponse(String token, String refreshToken, String username) {
		this(token, refreshToken, username, false);
	}

	public LoginResponse(String token, String refreshToken, String username, boolean mustChangePassword) {
		this.token = token;
		this.refreshToken = refreshToken;
		this.username = username;
		this.mustChangePassword = mustChangePassword;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isMustChangePassword() {
		return mustChangePassword;
	}

	public void setMustChangePassword(boolean mustChangePassword) {
		this.mustChangePassword = mustChangePassword;
	}

}
