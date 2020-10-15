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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.isf.security.jwt.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OHSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private RequestCache requestCache = new HttpSessionRequestCache();

	private TokenProvider tokenProvider;

	public OHSimpleUrlAuthenticationSuccessHandler(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	private final Logger logger = LoggerFactory.getLogger(OHSimpleUrlAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response, 
      Authentication authentication) 
      throws ServletException, IOException {
  
        SavedRequest savedRequest
          = requestCache.getRequest(request, response);


        //response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + ";SameSite=none; Secure");
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(this.tokenProvider.createToken(authentication, true));
        loginResponse.setDisplayName(authentication.getName());
        ObjectMapper mapper = new ObjectMapper();

        response.getWriter().append(mapper.writeValueAsString(loginResponse));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(200);

        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            return;
        }
        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl()
          || (targetUrlParam != null
          && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }
        clearAuthenticationAttributes(request);

        authentication.getDetails();


    }

}
