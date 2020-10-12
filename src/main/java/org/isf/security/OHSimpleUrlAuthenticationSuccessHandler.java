package org.isf.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.isf.patient.rest.PatientController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

public class OHSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private RequestCache requestCache = new HttpSessionRequestCache();

    private final Logger logger = LoggerFactory.getLogger(OHSimpleUrlAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response, 
      Authentication authentication) 
      throws ServletException, IOException {
  
        SavedRequest savedRequest
          = requestCache.getRequest(request, response);

        addSameSiteCookieAttribute(response);
        // response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + ";SameSite=none; Secure");
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

    private HttpServletResponse addSameSiteCookieAttribute(HttpServletResponse response) {
        Collection<String> header = response.getHeaders(HttpHeaders.SET_COOKIE);
        logger.info(String.format("%s; %s", header, "SameSite=None; Secure"));
        response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=None; Secure"));

        return response;
    }

}
