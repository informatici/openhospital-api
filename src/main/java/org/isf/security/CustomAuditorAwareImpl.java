package org.isf.security;

import java.util.Optional;

import org.isf.utils.db.AuditorAwareInterface;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomAuditorAwareImpl implements AuditorAwareInterface {

	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			return Optional.of(authentication.getName());
		} else {
			return Optional.empty();
		}
	}
}