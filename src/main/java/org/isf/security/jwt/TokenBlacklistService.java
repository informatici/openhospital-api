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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * In-memory blacklist of revoked token families.
 * <p>
 * A token family is identified by the {@code jti} claim shared by the access and the refresh token minted at login (and propagated by the refresh endpoint),
 * so revoking the family invalidates every token descending from that login. Entries are kept until the longest possible lifetime of any token belonging to
 * the family has elapsed; after that, the tokens are rejected as expired anyway. Eviction is lazy: stale entries are swept on each revocation and self-evict
 * when queried.
 */
@Component
public class TokenBlacklistService {

	private final Map<String, Long> revokedTokenFamilies = new ConcurrentHashMap<>();

	/**
	 * Blacklists the given token family.
	 *
	 * @param tokenFamilyId the {@code jti} claim shared by all tokens of the family
	 * @param retainForMilliseconds how long the entry must be retained; must cover the longest validity of any token of the family
	 */
	public void revoke(String tokenFamilyId, long retainForMilliseconds) {
		long now = System.currentTimeMillis();
		revokedTokenFamilies.entrySet().removeIf(entry -> entry.getValue() <= now);
		revokedTokenFamilies.put(tokenFamilyId, now + retainForMilliseconds);
	}

	/**
	 * Checks whether the given token family has been revoked.
	 *
	 * @param tokenFamilyId the {@code jti} claim of the token, or {@code null} for tokens minted before revocation support
	 * @return {@code true} if the family is blacklisted, {@code false} otherwise (always {@code false} for {@code null})
	 */
	public boolean isRevoked(String tokenFamilyId) {
		if (tokenFamilyId == null) {
			return false;
		}
		Long evictAt = revokedTokenFamilies.get(tokenFamilyId);
		if (evictAt == null) {
			return false;
		}
		if (evictAt <= System.currentTimeMillis()) {
			revokedTokenFamilies.remove(tokenFamilyId, evictAt);
			return false;
		}
		return true;
	}

	int size() {
		return revokedTokenFamilies.size();
	}

	void clear() {
		revokedTokenFamilies.clear();
	}
}
