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
package org.isf.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The password policy enforced by the server, so clients can validate passwords consistently")
public class PasswordPolicyDTO {

	@Schema(description = "Whether the strong-password policy (character requirements) is enforced", example = "true")
	private boolean strongPasswordEnabled;

	@Schema(description = "The minimum password length, or 0 when no minimum-length policy is enforced", example = "6")
	private int minLength;

	@Schema(description = "The regular expression a password must match when the strong-password policy is enabled")
	private String regex;

	public PasswordPolicyDTO() {
	}

	public PasswordPolicyDTO(boolean strongPasswordEnabled, int minLength, String regex) {
		this.strongPasswordEnabled = strongPasswordEnabled;
		this.minLength = minLength;
		this.regex = regex;
	}

	public boolean isStrongPasswordEnabled() {
		return strongPasswordEnabled;
	}

	public void setStrongPasswordEnabled(boolean strongPasswordEnabled) {
		this.strongPasswordEnabled = strongPasswordEnabled;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
