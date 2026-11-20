/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * This program is free and open source software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 */
package org.isf.login.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

	private MutableClock clock;
	private LoginAttemptService loginAttemptService;

	@BeforeEach
	void setUp() {
		clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
		loginAttemptService = new LoginAttemptService(3, Duration.ofSeconds(60), 100, clock);
	}

	@Test
	void blocksUsernameAndAddressPairAfterConfiguredFailures() {
		loginAttemptService.loginFailed("Admin", "192.0.2.1");
		loginAttemptService.loginFailed("admin", "192.0.2.1");
		loginAttemptService.loginFailed(" admin ", "192.0.2.1");

		assertThat(loginAttemptService.retryAfterSeconds("ADMIN", "192.0.2.1")).isEqualTo(60);
		assertThat(loginAttemptService.retryAfterSeconds("admin", "192.0.2.2")).isZero();
	}

	@Test
	void blocksAnAddressThatTriesMultipleUsernames() {
		loginAttemptService.loginFailed("user-one", "192.0.2.1");
		loginAttemptService.loginFailed("user-two", "192.0.2.1");
		loginAttemptService.loginFailed("user-three", "192.0.2.1");

		assertThat(loginAttemptService.retryAfterSeconds("another-user", "192.0.2.1")).isEqualTo(60);
	}

	@Test
	void allowsLoginAfterWindowExpires() {
		loginAttemptService.loginFailed("admin", "192.0.2.1");
		loginAttemptService.loginFailed("admin", "192.0.2.1");
		loginAttemptService.loginFailed("admin", "192.0.2.1");
		clock.advance(Duration.ofSeconds(61));

		assertThat(loginAttemptService.retryAfterSeconds("admin", "192.0.2.1")).isZero();
	}

	private static class MutableClock extends Clock {

		private Instant instant;

		MutableClock(Instant instant) {
			this.instant = instant;
		}

		void advance(Duration duration) {
			instant = instant.plus(duration);
		}

		@Override
		public ZoneId getZone() {
			return ZoneId.of("UTC");
		}

		@Override
		public Clock withZone(ZoneId zone) {
			return this;
		}

		@Override
		public Instant instant() {
			return instant;
		}
	}
}
