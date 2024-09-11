package org.isf.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
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
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
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

	@Test
	public void testValidateToken_Unsupported() throws Exception {
		KeyPair keyPair = generateRsaKeyPair();

		// Create a JWT token signed with RS256 (RSA algorithm) instead of HS512
		String unsupportedToken = Jwts.builder()
						.setSubject("testuser")
						.signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
						.compact();

		TokenValidationResult result = tokenProvider.validateToken(unsupportedToken);

		assertEquals(TokenValidationResult.UNSUPPORTED, result);
	}

	@Test
	public void testValidateToken_EmptyClaims() throws Exception {
		Field keyField = TokenProvider.class.getDeclaredField("key");
		keyField.setAccessible(true);
		Key key = (Key) keyField.get(tokenProvider);

		// Create a token with empty claims
		String emptyClaimsToken = Jwts.builder()
						.setSubject("") // Set empty subject (claims are present but empty)
						.claim("auth", "") // Set empty authority claims
						.signWith(key, SignatureAlgorithm.HS512)
						.compact();

		TokenValidationResult result = tokenProvider.validateToken(emptyClaimsToken);

		assertEquals(TokenValidationResult.EMPTY_CLAIMS, result);
	}

	@Test
	public void testGetAuthentication() {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		// Generate token
		String token = tokenProvider.generateJwtToken(authentication, false);

		// Retrieve Authentication object from token
		Authentication result = tokenProvider.getAuthentication(token);

		// Verify that the result is of the correct type and has the expected details
		assertTrue(result instanceof UsernamePasswordAuthenticationToken);

		UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) result;

		// Check principal
		assertEquals("testuser", ((User) authToken.getPrincipal()).getUsername());

		// Check authorities
		Collection< ? extends GrantedAuthority> resultAuthorities = authToken.getAuthorities();
		assertTrue(resultAuthorities.contains(new SimpleGrantedAuthority("ROLE_USER")));

		// Check credentials
		assertEquals(token, authToken.getCredentials());
	}

	@Test
	public void testSetJwtParser() {
		JwtParser mockJwtParser = mock(JwtParser.class);

		tokenProvider.setJwtParser(mockJwtParser);
	}

	@Test
	public void testGetUsernameFromToken() {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		String token = tokenProvider.generateJwtToken(authentication, false);
		String username = tokenProvider.getUsernameFromToken(token);

		assertEquals("testuser", username);
	}

	@Test
	public void testGetAllClaimsFromToken() {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		String token = tokenProvider.generateJwtToken(authentication, false);
		Claims claims = tokenProvider.getAllClaimsFromToken(token);

		assertNotNull(claims);
		assertEquals("testuser", claims.getSubject());
	}

	@Test
	public void testGetExpirationDateFromToken() {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		String token = tokenProvider.generateJwtToken(authentication, false);
		Date expirationDate = tokenProvider.getExpirationDateFromToken(token);

		assertNotNull(expirationDate);
		assertTrue(expirationDate.after(new Date()));
	}

	@Test
	public void testGetClaimFromToken() {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		// Generate token
		String token = tokenProvider.generateJwtToken(authentication, false);

		// Get claim from token
		String subject = tokenProvider.getClaimFromToken(token, Claims::getSubject);

		// Assert the claim
		assertEquals("testuser", subject);
	}

	@Test
	public void testIsTokenExpired() throws Exception {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

		Field keyField = TokenProvider.class.getDeclaredField("key");
		keyField.setAccessible(true);
		Key key = (Key) keyField.get(tokenProvider);

		// Generate a token with a future expiration date
		String token = tokenProvider.generateJwtToken(authentication, false);

		// Adjust the token to be expired by manually creating an expired token
		Date now = new Date();
		Date expiredDate = new Date(now.getTime() - 1000); // 1 second in the past
		String expiredToken = Jwts.builder()
						.setClaims(Jwts.parserBuilder()
										.setSigningKey(key)
										.build()
										.parseClaimsJws(token)
										.getBody())
						.setExpiration(expiredDate)
						.signWith(key, SignatureAlgorithm.HS512)
						.compact();

		// Test if the token is expired
		Boolean isExpired = tokenProvider.isTokenExpired(expiredToken);
		assertTrue(isExpired);
	}

	@Test
	public void testGenerateJwtToken_WithRememberMe() {
		// Create an Authentication object with mock authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);
		boolean rememberMe = true;

		String token = tokenProvider.generateJwtToken(authentication, rememberMe);
		Date now = new Date();
		Date expectedExpirationDate = new Date(now.getTime() + tokenProvider.getTokenValidityInMillisecondsForRememberMe());

		// Retrieve expiration date from the token
		Date actualExpirationDate = tokenProvider.getExpirationDateFromToken(token);

		// Assert
		long allowedSkew = 1000L; // Allow for a 1-second skew
		assertEquals(expectedExpirationDate.getTime(), actualExpirationDate.getTime(), allowedSkew);
	}

	// Helper method to generate RSA key pair
	private KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		return keyPairGenerator.generateKeyPair();
	}
}
