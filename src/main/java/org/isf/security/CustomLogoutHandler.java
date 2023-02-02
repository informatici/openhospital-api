/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.security;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.isf.sessionaudit.manager.SessionAuditManager;
import org.isf.sessionaudit.model.SessionAudit;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class CustomLogoutHandler implements LogoutHandler {
	@Autowired
	private HttpSession httpSession;

	@Autowired
	private SessionAuditManager sessionAuditManager;

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CustomLogoutHandler.class);

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		try {
			Optional<Object> sessionAuditIdOpt = Optional.ofNullable(httpSession.getAttribute("sessionAuditId"));
			if (sessionAuditIdOpt.isPresent()) {
				int sessionAuditId = (int) sessionAuditIdOpt.get();
				Optional<SessionAudit> sa = sessionAuditManager.getSessionAudit(sessionAuditId);
				if (sa.isPresent()) {
					SessionAudit sessionAudit = sa.get();
					sessionAudit.setLogoutDate(LocalDateTime.now());
					sessionAuditManager.updateSessionAudit(sessionAudit);
				}
			} else {
				LOGGER.error("Unable to find the session. Are you sure that you are logged in?");
			}
		} catch (OHServiceException e) {
			LOGGER.error("Unable to log user login timestamp in the session_audit table");
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContext context = SecurityContextHolder.getContext();
		SecurityContextHolder.clearContext();
		context.setAuthentication(null);
	}
}
