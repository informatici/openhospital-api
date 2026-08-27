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
package org.isf.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenBlacklistServiceTest {

	private static final long RETENTION = 60_000L;

	private TokenBlacklistService tokenBlacklistService;

	@BeforeEach
	void setUp() {
		tokenBlacklistService = new TokenBlacklistService();
	}

	@Test
	void testIsRevoked_RevokedFamily() {
		tokenBlacklistService.revoke("family-id", RETENTION);

		assertThat(tokenBlacklistService.isRevoked("family-id")).isTrue();
	}

	@Test
	void testIsRevoked_UnknownFamily() {
		tokenBlacklistService.revoke("family-id", RETENTION);

		assertThat(tokenBlacklistService.isRevoked("other-family-id")).isFalse();
	}

	@Test
	void testIsRevoked_NullFamily() {
		// tokens minted before revocation support have no jti claim: they must never appear revoked
		tokenBlacklistService.revoke("family-id", RETENTION);

		assertThat(tokenBlacklistService.isRevoked(null)).isFalse();
	}

	@Test
	void testIsRevoked_ExpiredEntrySelfEvicts() {
		// a zero retention makes the entry immediately stale
		tokenBlacklistService.revoke("family-id", 0L);

		assertThat(tokenBlacklistService.isRevoked("family-id")).isFalse();
		assertThat(tokenBlacklistService.size()).isZero();
	}

	@Test
	void testRevoke_SweepsStaleEntries() {
		tokenBlacklistService.revoke("stale-family-id", 0L);
		assertThat(tokenBlacklistService.size()).isEqualTo(1);

		tokenBlacklistService.revoke("family-id", RETENTION);

		assertThat(tokenBlacklistService.size()).isEqualTo(1);
		assertThat(tokenBlacklistService.isRevoked("stale-family-id")).isFalse();
		assertThat(tokenBlacklistService.isRevoked("family-id")).isTrue();
	}

	@Test
	void testRevoke_Idempotent() {
		tokenBlacklistService.revoke("family-id", RETENTION);
		tokenBlacklistService.revoke("family-id", RETENTION);

		assertThat(tokenBlacklistService.isRevoked("family-id")).isTrue();
		assertThat(tokenBlacklistService.size()).isEqualTo(1);
	}

	@Test
	void testClear() {
		tokenBlacklistService.revoke("family-id", RETENTION);
		tokenBlacklistService.clear();

		assertThat(tokenBlacklistService.isRevoked("family-id")).isFalse();
		assertThat(tokenBlacklistService.size()).isZero();
	}
}
