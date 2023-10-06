/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.config;

import java.util.Arrays;

import org.isf.security.CustomLogoutHandler;
import org.isf.security.OHSimpleUrlAuthenticationSuccessHandler;
import org.isf.security.RestAuthenticationEntryPoint;
import org.isf.security.jwt.JWTConfigurer;
import org.isf.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	@Autowired
	private UserDetailsService userDetailsService;

	private final TokenProvider tokenProvider;

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	public SecurityConfig(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Autowired
	private CustomLogoutHandler customLogoutHandler;

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedHeader("*");
		// config.setAllowedHeaders(Arrays.asList("Accept", "Accept-Encoding", "Accept-Language", "Authorization", "Content-Type", "Cache-Control",
		// "Connection", "Cookie", "Host", "Pragma", "Referer, User-Agent"));
		config.setAllowedMethods(Arrays.asList("*"));
		// config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.setMaxAge(3600L);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						.and().cors()
						.and().csrf().disable().authorizeRequests()
						// .expressionHandler(webExpressionHandler())
						.and().exceptionHandling()
						// .accessDeniedHandler(accessDeniedHandler)
						.authenticationEntryPoint(restAuthenticationEntryPoint)
						.and().authorizeRequests().antMatchers("/auth/**").permitAll()

						// patients
						.antMatchers(HttpMethod.POST, "/patients/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/patients/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/patients/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/patients/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/patients/**").hasAnyAuthority("admin", "guest", "doctor")
						// admissiontypes
						.antMatchers(HttpMethod.POST, "/admissiontypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/admissiontypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/admissiontypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/admissiontypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/admissiontypes/**").hasAnyAuthority("admin", "guest", "doctor")
						// deliveryresulttype
						.antMatchers(HttpMethod.POST, "/deliveryresulttype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/deliveryresulttype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/deliveryresulttype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/deliveryresulttype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/deliveryresulttype/**").hasAnyAuthority("admin", "guest", "doctor")
						// deliverytypes
						.antMatchers(HttpMethod.POST, "/deliverytypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/deliverytypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/deliverytypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/deliverytypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/deliverytypes/**").hasAnyAuthority("admin", "guest", "doctor")
						// dischargetypes
						.antMatchers(HttpMethod.POST, "/dischargetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/dischargetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/dischargetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/dischargetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/dischargetypes/**").hasAnyAuthority("admin", "guest", "doctor")
						// admissions
						.antMatchers(HttpMethod.POST, "/admissions/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/admissions/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/admissions/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/admissions/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/admissions/**").hasAnyAuthority("admin", "guest", "doctor")
						// discharges
						.antMatchers(HttpMethod.POST, "/discharges/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/discharges/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/discharges/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/discharges/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/discharges/**").hasAnyAuthority("admin", "guest", "doctor")
						// vaccines
						.antMatchers(HttpMethod.POST, "/vaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/vaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/vaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/vaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/vaccines/**").hasAnyAuthority("admin", "guest", "doctor")
						// vaccineType
						.antMatchers(HttpMethod.POST, "/vaccinetype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/vaccinetype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/vaccinetype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/vaccinetype/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/vaccinetype/**").hasAnyAuthority("admin", "guest", "doctor")
						// visit
						.antMatchers(HttpMethod.POST, "/visit/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/visit/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/visit/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/visit/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/visit/**").hasAnyAuthority("admin", "guest", "doctor")
						// wards
						.antMatchers(HttpMethod.POST, "/wards/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/wards/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/wards/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/wards/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/wards/**").hasAnyAuthority("admin", "guest", "doctor")
						// exams
						.antMatchers(HttpMethod.POST, "/exams/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.PUT, "/exams/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.DELETE, "/exams/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.PATCH, "/exams/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.GET, "/exams/**").hasAnyAuthority("admin", "guest", "laboratorist", "doctor")
						// examrows
						.antMatchers(HttpMethod.POST, "/examrows/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.PUT, "/examrows/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.DELETE, "/examrows/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.PATCH, "/examrows/**").hasAnyAuthority("admin", "laboratorist", "doctor")
						.antMatchers(HttpMethod.GET, "/examrows/**").hasAnyAuthority("admin", "guest", "laboratorist", "doctor")
						// examtypes
						.antMatchers(HttpMethod.POST, "/examtypes/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.PUT, "/examtypes/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.DELETE, "/examtypes/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.PATCH, "/examtypes/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.GET, "/examtypes/**").hasAnyAuthority("admin", "guest", "laboratorist")
						// examinations
						.antMatchers(HttpMethod.POST, "/examinations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/examinations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/examinations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/examinations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/examinations/**").hasAnyAuthority("admin", "guest", "doctor")
						// hospitals
						.antMatchers(HttpMethod.POST, "/hospitals/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/hospitals/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/hospitals/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/hospitals/**").hasAnyAuthority("admin", "doctor")
						//.antMatchers(HttpMethod.GET, "/hospitals/**").hasAnyAuthority("admin", "guest", "doctor", "laboratorist")
						// laboratories
						.antMatchers(HttpMethod.POST, "/laboratories/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.PUT, "/laboratories/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.DELETE, "/laboratories/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.PATCH, "/laboratories/**").hasAnyAuthority("admin", "laboratorist")
						.antMatchers(HttpMethod.GET, "/laboratories/**").hasAnyAuthority("admin", "guest", "laboratorist")
						// .antMatchers("/auth-needed/**").authenticated()
						// .antMatchers("/noauth-public/**").permitAll()
						// .antMatchers("/admin/**").hasAuthority("admin")
						// age types
						.antMatchers(HttpMethod.PUT, "/agetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/agetypes/**").hasAnyAuthority("admin", "guest", "doctor")
						// disease types
						.antMatchers(HttpMethod.POST, "/diseasetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/diseasetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/diseasetypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/diseasetypes/**").hasAnyAuthority("admin", "guest", "doctor")
						// opd
						.antMatchers(HttpMethod.POST, "/opds/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/opds/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/opds/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/opds/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/opds/**").hasAnyAuthority("admin", "guest", "doctor")
						// operations
						.antMatchers(HttpMethod.POST, "/operations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/operations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/operations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/operations/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/operations/**").hasAnyAuthority("admin", "guest", "doctor")
						// patientvaccines
						.antMatchers(HttpMethod.POST, "/patientvaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/patientvaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/patientvaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/patientvaccines/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/patientvaccines/**").hasAnyAuthority("admin", "guest", "doctor")
						// pregnanttreatmenttypes
						.antMatchers(HttpMethod.POST, "/pregnanttreatmenttypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/pregnanttreatmenttypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/pregnanttreatmenttypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/pregnanttreatmenttypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/pregnanttreatmenttypes/**").hasAnyAuthority("admin", "guest", "doctor")
						// pricelists
						.antMatchers(HttpMethod.POST, "/pricelists/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/pricelists/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/pricelists/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/pricelists/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/pricelists/**").hasAnyAuthority("admin", "guest", "doctor")
						// pricesothers
						.antMatchers(HttpMethod.POST, "/pricesothers/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/pricesothers/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/pricesothers/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/pricesothers/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/pricesothers/**").hasAnyAuthority("admin", "guest", "doctor")
						// operation types
						.antMatchers(HttpMethod.POST, "/operationtypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/operationtypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/operationtypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PATCH, "/operationtypes/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/operationtypes/**").hasAnyAuthority("admin", "guest", "doctor")
						// diseases
						.antMatchers(HttpMethod.POST, "/diseases/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.PUT, "/diseases/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.DELETE, "/diseases/**").hasAnyAuthority("admin", "doctor")
						.antMatchers(HttpMethod.GET, "/diseases/**").hasAnyAuthority("admin", "guest", "doctor")
//			.and()
//			.formLogin()
//				 .loginPage("/auth/login")
//				 .successHandler(successHandler())
//				 .failureHandler(failureHandler())
						.and().apply(securityConfigurerAdapter())
						.and().httpBasic()
						.and().logout().logoutUrl("/auth/logout").addLogoutHandler(customLogoutHandler).permitAll();
		return http.build();
	}

	private JWTConfigurer securityConfigurerAdapter() {
		return new JWTConfigurer(tokenProvider);
	}

	@Bean
	public SimpleUrlAuthenticationFailureHandler failureHandler() {
		return new SimpleUrlAuthenticationFailureHandler();
	}

	@Bean
	public SimpleUrlAuthenticationSuccessHandler successHandler() {
		return new OHSimpleUrlAuthenticationSuccessHandler(tokenProvider);
	}

	private SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
		DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
		defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
		return defaultWebSecurityExpressionHandler;
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		String hierarchy = "ROLE_ADMIN > ROLE_FAMILYMANAGER \n ROLE_FAMILYMANAGER > ROLE_USER";
		roleHierarchy.setHierarchy(hierarchy);
		return roleHierarchy;
	}
}
