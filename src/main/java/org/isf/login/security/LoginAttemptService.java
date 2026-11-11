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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Tracks failed logins in a bounded, in-memory store. Limits are applied both to a client address and to a username/address pair.
 */
@Service
public class LoginAttemptService {

	private static final String ADDRESS_PREFIX = "address:";
	private static final String USER_ADDRESS_PREFIX = "user-address:";

	private final Map<String, AttemptWindow> attempts = new ConcurrentHashMap<>();
	private final int maxAttempts;
	private final Duration window;
	private final int maxEntries;
	private final Clock clock;

	@Autowired
	public LoginAttemptService(
		@Value("${security.login-rate-limit.max-attempts:5}") int maxAttempts,
		@Value("${security.login-rate-limit.window-seconds:60}") long windowSeconds,
		@Value("${security.login-rate-limit.max-entries:10000}") int maxEntries
	) {
		this(maxAttempts, Duration.ofSeconds(windowSeconds), maxEntries, Clock.systemUTC());
	}

	LoginAttemptService(int maxAttempts, Duration window, int maxEntries, Clock clock) {
		if (maxAttempts <= 0 || window.isZero() || window.isNegative() || maxEntries < 2) {
			throw new IllegalArgumentException("Login rate-limit settings must be positive.");
		}
		this.maxAttempts = maxAttempts;
		this.window = window;
		this.maxEntries = maxEntries;
		this.clock = clock;
	}

	/**
	 * Returns the number of seconds before login can be retried, or zero when the request is allowed.
	 */
	public long retryAfterSeconds(String username, String clientAddress) {
		Instant now = clock.instant();
		long userAddressRetry = retryAfterSeconds(userAddressKey(username, clientAddress), now);
		long addressRetry = retryAfterSeconds(addressKey(clientAddress), now);
		return Math.max(userAddressRetry, addressRetry);
	}

	public void loginFailed(String username, String clientAddress) {
		Instant now = clock.instant();
		recordFailure(userAddressKey(username, clientAddress), now);
		recordFailure(addressKey(clientAddress), now);
	}

	public void loginSucceeded(String username, String clientAddress) {
		attempts.remove(userAddressKey(username, clientAddress));
	}

	private long retryAfterSeconds(String key, Instant now) {
		AttemptWindow attemptWindow = attempts.get(key);
		if (attemptWindow == null) {
			return 0;
		}
		if (!now.isBefore(attemptWindow.expiresAt())) {
			attempts.remove(key, attemptWindow);
			return 0;
		}
		if (attemptWindow.failures() < maxAttempts) {
			return 0;
		}
		long remainingMillis = Duration.between(now, attemptWindow.expiresAt()).toMillis();
		return Math.max(1, (remainingMillis + 999) / 1000);
	}

	private void recordFailure(String key, Instant now) {
		if (!attempts.containsKey(key) && attempts.size() >= maxEntries) {
			evictOneEntry(now);
		}
		attempts.compute(key, (ignored, current) -> {
			if (current == null || !now.isBefore(current.expiresAt())) {
				return new AttemptWindow(1, now.plus(window));
			}
			return new AttemptWindow(current.failures() + 1, current.expiresAt());
		});
	}

	private void evictOneEntry(Instant now) {
		attempts.entrySet().removeIf(entry -> !now.isBefore(entry.getValue().expiresAt()));
		if (attempts.size() < maxEntries) {
			return;
		}
		attempts.entrySet().stream()
			.min(Comparator.comparing(entry -> entry.getValue().expiresAt()))
			.ifPresent(entry -> attempts.remove(entry.getKey(), entry.getValue()));
	}

	private String userAddressKey(String username, String clientAddress) {
		String normalizedUsername = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
		return USER_ADDRESS_PREFIX + normalizedUsername + ':' + normalizeAddress(clientAddress);
	}

	private String addressKey(String clientAddress) {
		return ADDRESS_PREFIX + normalizeAddress(clientAddress);
	}

	private String normalizeAddress(String clientAddress) {
		return clientAddress == null || clientAddress.isBlank() ? "unknown" : clientAddress;
	}

	private record AttemptWindow(int failures, Instant expiresAt) {
	}
}
