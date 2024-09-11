/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class TokenProvider implements Serializable {

	private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

	@Autowired
	private Environment env;

	private static final String AUTHORITIES_KEY = "auth";

	private Key key;

	private long tokenValidityInMilliseconds;

	private long tokenValidityInMillisecondsForRememberMe;

	private JwtParser jwtParser;

	@PostConstruct
	public void init() {
		String secret = env.getProperty("jwt.token.secret");
		log.info("Initializing JWT key with secret: {}", secret);
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);

		this.tokenValidityInMilliseconds = 1000L * 6000;
		this.tokenValidityInMillisecondsForRememberMe = 1000L * 6000;

		this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
	}

	public long getTokenValidityInMillisecondsForRememberMe() {
		return tokenValidityInMillisecondsForRememberMe;
	}

	public void setJwtParser(JwtParser jwtParser) {
		this.jwtParser = jwtParser;
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Claims getAllClaimsFromToken(String token) {
		return this.jwtParser.parseClaimsJws(token).getBody();
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	//needed because jwtParser.parseClaimsJws throws an exception when token is expired
	public Boolean isTokenExpired(String token) {
		try {
			final Date expiration = getExpirationDateFromToken(token);
			return expiration.before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String generateJwtToken(Authentication authentication, boolean rememberMe) {
		final String authorities = authentication.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.joining(","));

		long now = System.currentTimeMillis();
		Date validity;
		if (rememberMe) {
			validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
		} else {
			validity = new Date(now + this.tokenValidityInMilliseconds);
		}

		return Jwts.builder()
						.setSubject(authentication.getName())
						.claim(AUTHORITIES_KEY, authorities)
						.signWith(key, SignatureAlgorithm.HS512)
						.setExpiration(validity)
						.compact();
	}

	public Authentication getAuthentication(String token) {
		final Claims claims = getAllClaimsFromToken(token);

		final Collection< ? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());

		User principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	public TokenValidationResult validateToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
			if (claims.getSubject() == null || claims.getSubject().isEmpty()) {
				throw new IllegalArgumentException("JWT claims string is empty");
			}
			return TokenValidationResult.VALID;
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
			return TokenValidationResult.MALFORMED;
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
			return TokenValidationResult.EXPIRED;
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
			return TokenValidationResult.UNSUPPORTED;
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			return TokenValidationResult.EMPTY_CLAIMS;
		} catch (SignatureException e) {
			log.error("JWT signature does not match locally computed signature: {}", e.getMessage());
			return TokenValidationResult.INVALID_SIGNATURE;
		}
	}
}
