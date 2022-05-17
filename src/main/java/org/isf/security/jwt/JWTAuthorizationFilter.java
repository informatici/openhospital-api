package org.isf.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.isf.menu.service.MenuIoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private String HEADER_STRING = SecurityConstants.HEADER_STRING;
	private String TOKEN_PREFIX = SecurityConstants.TOKEN_PREFIX;
	private String SECRET = SecurityConstants.SECRET;
	
	@Autowired
    private TokenProvider jwtTokenUtil;
	@Autowired
	private MenuIoOperations menuIoOperations;
	
    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }
    

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
        	 System.out.println("ici 1");
        	chain.doFilter(req, res);
            return;
        }
        System.out.println("ici 2");
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    // Reads the JWT from the Authorization header, and then uses JWT to validate the token
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            // parse the token.
            String LoginRequest =  Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (LoginRequest != null) {
                // new arraylist means authorities
                
                return new UsernamePasswordAuthenticationToken(LoginRequest, null,  AuthorityUtils.createAuthorityList("admin", "guest"));
            }

            return null;
        }

        return null;
    }
    
}
