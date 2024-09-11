package org.isf.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;
import java.util.List;

import org.isf.OpenHospitalApiApplication;
import org.isf.security.jwt.TokenProvider;
import org.isf.security.jwt.TokenValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
public class TokenProviderTest {

	@Autowired
	private TokenProvider tokenProvider;

	@BeforeEach
	public void setUp() {

		tokenProvider.init();
	}

	@Test
	public void testGenerateJwtToken() throws NoSuchFieldException, IllegalAccessException {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		// Generate token
		// Use reflection to access the private key field
		Field keyField = TokenProvider.class.getDeclaredField("key");
		keyField.setAccessible(true);
		Key key = (Key) keyField.get(tokenProvider);

		String token = tokenProvider.generateJwtToken(authentication, false);

		// Validate the generated token
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		// Assert the claims
		assertEquals("testuser", claims.getSubject());
		assertEquals("ROLE_USER", claims.get("auth"));
	}

	@Test
	public void testValidateToken_Valid() {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		// Generate token
		String token = tokenProvider.generateJwtToken(authentication, false);

		// Validate the token using tokenProvider
		TokenValidationResult result = tokenProvider.validateToken(token);

		// Assert that the token is valid
		assertEquals(TokenValidationResult.VALID, result);
	}

	@Test
	public void testValidateToken_Expired() throws Exception {
		// Use reflection to get the private key from tokenProvider
		Field keyField = TokenProvider.class.getDeclaredField("key");
		keyField.setAccessible(true);
		Key key = (Key) keyField.get(tokenProvider);

		// Create an expired token by setting the expiration date in the past
		String expiredToken = Jwts.builder()
						.setSubject("testuser")
						.claim("auth", "ROLE_USER")
						.signWith(key, SignatureAlgorithm.HS512)
						.setExpiration(new Date(System.currentTimeMillis() - 1000)) // Set expiration to past
						.compact();

		// Validate the expired token
		TokenValidationResult result = tokenProvider.validateToken(expiredToken);

		// Assert that the token is expired
		assertEquals(TokenValidationResult.EXPIRED, result);
	}

	@Test
	public void testValidateToken_Malformed() {
		String malformedToken = "malformed.token";
		TokenValidationResult result = tokenProvider.validateToken(malformedToken);
		assertEquals(TokenValidationResult.MALFORMED, result);
	}

	@Test
	public void testValidateToken_InvalidSignature() {
		String invalidSignatureToken = "eyJhbGciOiJIUzI1NiJ9.MISSING_PART.HMAC_SIGNATURE";
		TokenValidationResult result = tokenProvider.validateToken(invalidSignatureToken);
		assertEquals(TokenValidationResult.INVALID_SIGNATURE, result);
	}
}
