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

import org.isf.permissions.manager.PermissionManager;
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

	@Autowired
	protected PermissionManager permissionManager;
	
	public SecurityConfig(TokenProvider tokenProvider, PermissionManager permissionManager) {
		this.tokenProvider = tokenProvider;
		this.permissionManager = permissionManager;
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
						.antMatchers(HttpMethod.POST, "/patients/**").hasAuthority("patient.create")
						.antMatchers(HttpMethod.PUT, "/patients/**").hasAuthority("patient.update")
						.antMatchers(HttpMethod.DELETE, "/patients/**").hasAuthority("patient.delete")
						.antMatchers(HttpMethod.PATCH, "/patients/**").hasAuthority("patient.update")
						.antMatchers(HttpMethod.GET, "/patients/**").hasAuthority("patient.read")
						// admissiontypes
						.antMatchers(HttpMethod.POST, "/admissiontypes/**").hasAuthority("admissiontypes.create")
						.antMatchers(HttpMethod.PUT, "/admissiontypes/**").hasAuthority("admissiontypes.update")
						.antMatchers(HttpMethod.DELETE, "/admissiontypes/**").hasAuthority("admissiontypes.delete")
						.antMatchers(HttpMethod.PATCH, "/admissiontypes/**").hasAuthority("admissiontypes.update")
						.antMatchers(HttpMethod.GET, "/admissiontypes/**").hasAnyAuthority("admissiontypes.read")
						// deliveryresulttype
						.antMatchers(HttpMethod.POST, "/deliveryresulttype/**").hasAuthority("deliveryresulttype.create")
						.antMatchers(HttpMethod.PUT, "/deliveryresulttype/**").hasAuthority("deliveryresulttype.update")
						.antMatchers(HttpMethod.DELETE, "/deliveryresulttype/**").hasAuthority("deliveryresulttype.delete")
						.antMatchers(HttpMethod.PATCH, "/deliveryresulttype/**").hasAuthority("deliveryresulttype.update")
						.antMatchers(HttpMethod.GET, "/deliveryresulttype/**").hasAnyAuthority("deliveryresulttype.read")
						// deliverytypes
						.antMatchers(HttpMethod.POST, "/deliverytypes/**").hasAuthority("deliveryresulttype.create")
						.antMatchers(HttpMethod.PUT, "/deliverytypes/**").hasAuthority("deliveryresulttype.update")
						.antMatchers(HttpMethod.DELETE, "/deliverytypes/**").hasAuthority("deliveryresulttype.delete")
						.antMatchers(HttpMethod.PATCH, "/deliverytypes/**").hasAuthority("deliveryresulttype.update")
						.antMatchers(HttpMethod.GET, "/deliverytypes/**").hasAnyAuthority("deliveryresulttype.read")
						// dischargetypes
						.antMatchers(HttpMethod.POST, "/dischargetypes/**").hasAuthority("dischargetypes.create")
						.antMatchers(HttpMethod.PUT, "/dischargetypes/**").hasAuthority("dischargetypes.update")
						.antMatchers(HttpMethod.DELETE, "/dischargetypes/**").hasAuthority("dischargetypes.delete")
						.antMatchers(HttpMethod.PATCH, "/dischargetypes/**").hasAuthority("dischargetypes.update")
						.antMatchers(HttpMethod.GET, "/dischargetypes/**").hasAnyAuthority("dischargetypes.read")
						// admissions
						.antMatchers(HttpMethod.POST, "/admissions/**").hasAuthority("admission.create")
						.antMatchers(HttpMethod.PUT, "/admissions/**").hasAuthority("admission.update")
						.antMatchers(HttpMethod.DELETE, "/admissions/**").hasAuthority("admission.delete")
						.antMatchers(HttpMethod.PATCH, "/admissions/**").hasAuthority("admission.update")
						.antMatchers(HttpMethod.GET, "/admissions/**").hasAnyAuthority("admission.read")
						// discharges
						.antMatchers(HttpMethod.POST, "/discharges/**").hasAuthority("discharges.create")
						.antMatchers(HttpMethod.PUT, "/discharges/**").hasAuthority("discharges.update")
						.antMatchers(HttpMethod.DELETE, "/discharges/**").hasAuthority("discharges.delete")
						.antMatchers(HttpMethod.PATCH, "/discharges/**").hasAuthority("discharges.update")
						.antMatchers(HttpMethod.GET, "/discharges/**").hasAnyAuthority("discharges.read")
						// vaccines
						.antMatchers(HttpMethod.POST, "/vaccines/**").hasAuthority("vaccines.create")
						.antMatchers(HttpMethod.PUT, "/vaccines/**").hasAuthority("vaccines.update")
						.antMatchers(HttpMethod.DELETE, "/vaccines/**").hasAuthority("vaccines.delete")
						.antMatchers(HttpMethod.PATCH, "/vaccines/**").hasAuthority("vaccines.update")
						.antMatchers(HttpMethod.GET, "/vaccines/**").hasAnyAuthority("vaccines.read")
						// vaccineType
						.antMatchers(HttpMethod.POST, "/vaccinetype/**").hasAuthority("vaccinetype.create")
						.antMatchers(HttpMethod.PUT, "/vaccinetype/**").hasAuthority("vaccinetype.update")
						.antMatchers(HttpMethod.DELETE, "/vaccinetype/**").hasAuthority("vaccinetype.delete")
						.antMatchers(HttpMethod.PATCH, "/vaccinetype/**").hasAuthority("vaccinetype.update")
						.antMatchers(HttpMethod.GET, "/vaccinetype/**").hasAnyAuthority("vaccinetype.read")
						// visit
						.antMatchers(HttpMethod.POST, "/visit/**").hasAuthority("visit.access")
						.antMatchers(HttpMethod.PUT, "/visit/**").hasAuthority("visit.access")
						.antMatchers(HttpMethod.DELETE, "/visit/**").hasAuthority("visit.access")
						.antMatchers(HttpMethod.PATCH, "/visit/**").hasAuthority("visit.access")
						.antMatchers(HttpMethod.GET, "/visit/**").hasAnyAuthority("visit.access")
						// wards
						.antMatchers(HttpMethod.POST, "/wards/**").hasAuthority("wards.create")
						.antMatchers(HttpMethod.PUT, "/wards/**").hasAuthority("wards.update")
						.antMatchers(HttpMethod.DELETE, "/wards/**").hasAuthority("wards.delete")
						.antMatchers(HttpMethod.PATCH, "/wards/**").hasAuthority("wards.update")
						.antMatchers(HttpMethod.GET, "/wards/**").hasAnyAuthority("wards.read")
						// exams
						.antMatchers(HttpMethod.POST, "/exams/**").hasAuthority("exam.create")
						.antMatchers(HttpMethod.PUT, "/exams/**").hasAuthority("exam.update")
						.antMatchers(HttpMethod.DELETE, "/exams/**").hasAuthority("exam.delete")
						.antMatchers(HttpMethod.PATCH, "/exams/**").hasAuthority("exam.update")
						.antMatchers(HttpMethod.GET, "/exams/**").hasAnyAuthority("exam.read")
						// examrows
						.antMatchers(HttpMethod.POST, "/examrows/**").hasAuthority("examrows.create")
						.antMatchers(HttpMethod.PUT, "/examrows/**").hasAuthority("examrows.update")
						.antMatchers(HttpMethod.DELETE, "/examrows/**").hasAuthority("examrows.delete")
						.antMatchers(HttpMethod.PATCH, "/examrows/**").hasAuthority("examrows.update")
						.antMatchers(HttpMethod.GET, "/examrows/**").hasAnyAuthority("examrows.read")
						// examtypes
						.antMatchers(HttpMethod.POST, "/examtypes/**").hasAuthority("examtypes.create")
						.antMatchers(HttpMethod.PUT, "/examtypes/**").hasAuthority("examtypes.update")
						.antMatchers(HttpMethod.DELETE, "/examtypes/**").hasAuthority("examtypes.delete")
						.antMatchers(HttpMethod.PATCH, "/examtypes/**").hasAuthority("examtypes.update")
						.antMatchers(HttpMethod.GET, "/examtypes/**").hasAnyAuthority("examtypes.read")
						// examinations
						.antMatchers(HttpMethod.POST, "/examinations/**").hasAuthority("examinations.create")
						.antMatchers(HttpMethod.PUT, "/examinations/**").hasAuthority("examinations.update")
						.antMatchers(HttpMethod.DELETE, "/examinations/**").hasAuthority("examinations.delete")
						.antMatchers(HttpMethod.PATCH, "/examinations/**").hasAuthority("examinations.update")
						.antMatchers(HttpMethod.GET, "/examinations/**").hasAnyAuthority("examinations.read")
						// hospitals
						.antMatchers(HttpMethod.POST, "/hospitals/**").hasAuthority("hospitals.create")
						.antMatchers(HttpMethod.PUT, "/hospitals/**").hasAuthority("hospitals.update")
						.antMatchers(HttpMethod.DELETE, "/hospitals/**").hasAuthority("hospitals.delete")
						.antMatchers(HttpMethod.PATCH, "/hospitals/**").hasAuthority("hospitals.update")
						// .antMatchers(HttpMethod.GET, "/hospitals/**").hasAnyAuthority("admin", "guest") to anyone
						// laboratories
						.antMatchers(HttpMethod.POST, "/laboratories/**").hasAuthority("laboratories.create")
						.antMatchers(HttpMethod.PUT, "/laboratories/**").hasAuthority("laboratories.update")
						.antMatchers(HttpMethod.DELETE, "/laboratories/**").hasAuthority("laboratories.delete")
						.antMatchers(HttpMethod.PATCH, "/laboratories/**").hasAuthority("laboratories.update")
						.antMatchers(HttpMethod.GET, "/laboratories/**").hasAnyAuthority("laboratories.read")
						// .antMatchers("/auth-needed/**").authenticated()
						// .antMatchers("/noauth-public/**").permitAll()
						// .antMatchers("/admin/**").hasAuthority("admin")
						// age types
						.antMatchers(HttpMethod.PUT, "/agetypes/**").hasAuthority("agetypes.update")
						.antMatchers(HttpMethod.GET, "/agetypes/**").hasAnyAuthority("agetypes.read")
						// disease types
						.antMatchers(HttpMethod.POST, "/diseasetypes/**").hasAuthority("diseasetypes.create")
						.antMatchers(HttpMethod.PUT, "/diseasetypes/**").hasAuthority("diseasetypes.update")
						.antMatchers(HttpMethod.DELETE, "/diseasetypes/**").hasAuthority("diseasetypes.delete")
						.antMatchers(HttpMethod.GET, "/diseasetypes/**").hasAnyAuthority("diseasetypes.read")
						// opd
						.antMatchers(HttpMethod.POST, "/opds/**").hasAuthority("opd.create")
						.antMatchers(HttpMethod.PUT, "/opds/**").hasAuthority("opd.update")
						.antMatchers(HttpMethod.DELETE, "/opds/**").hasAuthority("opd.delete")
						.antMatchers(HttpMethod.PATCH, "/opds/**").hasAuthority("opd.update")
						.antMatchers(HttpMethod.GET, "/opds/**").hasAnyAuthority("opd.read")
						// operations
						.antMatchers(HttpMethod.POST, "/operations/**").hasAuthority("operations.create")
						.antMatchers(HttpMethod.PUT, "/operations/**").hasAuthority("operations.update")
						.antMatchers(HttpMethod.DELETE, "/operations/**").hasAuthority("operations.delete")
						.antMatchers(HttpMethod.PATCH, "/operations/**").hasAuthority("operations.update")
						.antMatchers(HttpMethod.GET, "/operations/**").hasAnyAuthority("operations.read")
						// patientvaccines
						.antMatchers(HttpMethod.POST, "/patientvaccines/**").hasAuthority("patientvaccines.create")
						.antMatchers(HttpMethod.PUT, "/patientvaccines/**").hasAuthority("patientvaccines.update")
						.antMatchers(HttpMethod.DELETE, "/patientvaccines/**").hasAuthority("patientvaccines.delete")
						.antMatchers(HttpMethod.PATCH, "/patientvaccines/**").hasAuthority("patientvaccines.update")
						.antMatchers(HttpMethod.GET, "/patientvaccines/**").hasAnyAuthority("patientvaccines.read")
						// pregnanttreatmenttypes
						.antMatchers(HttpMethod.POST, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.create")
						.antMatchers(HttpMethod.PUT, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.update")
						.antMatchers(HttpMethod.DELETE, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.delete")
						.antMatchers(HttpMethod.PATCH, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.update")
						.antMatchers(HttpMethod.GET, "/pregnanttreatmenttypes/**").hasAnyAuthority("pregnanttreatmenttypes.read")
						// pricelists
						.antMatchers(HttpMethod.POST, "/pricelists/**").hasAuthority("pricelists.create")
						.antMatchers(HttpMethod.PUT, "/pricelists/**").hasAuthority("pricelists.update")
						.antMatchers(HttpMethod.DELETE, "/pricelists/**").hasAuthority("pricelists.delete")
						.antMatchers(HttpMethod.PATCH, "/pricelists/**").hasAuthority("pricelists.update")
						.antMatchers(HttpMethod.GET, "/pricelists/**").hasAnyAuthority("pricelists.read")
						// pricesothers
						.antMatchers(HttpMethod.POST, "/pricesothers/**").hasAuthority("pricesothers.create")
						.antMatchers(HttpMethod.PUT, "/pricesothers/**").hasAuthority("pricesothers.update")
						.antMatchers(HttpMethod.DELETE, "/pricesothers/**").hasAuthority("pricesothers.delete")
						.antMatchers(HttpMethod.PATCH, "/pricesothers/**").hasAuthority("pricesothers.update")
						.antMatchers(HttpMethod.GET, "/pricesothers/**").hasAnyAuthority("pricesothers.read")
						// operation types
						.antMatchers(HttpMethod.POST, "/operationtypes/**").hasAuthority("operationtypes.create")
						.antMatchers(HttpMethod.PUT, "/operationtypes/**").hasAuthority("operationtypes.update")
						.antMatchers(HttpMethod.DELETE, "/operationtypes/**").hasAuthority("operationtypes.delete")
						.antMatchers(HttpMethod.PATCH, "/operationtypes/**").hasAuthority("operationtypes.update")
						.antMatchers(HttpMethod.GET, "/operationtypes/**").hasAnyAuthority("operationtypes.read")
						// diseases
						.antMatchers(HttpMethod.POST, "/diseases/**").hasAuthority("diseases.create")
						.antMatchers(HttpMethod.PUT, "/diseases/**").hasAuthority("diseases.update")
						.antMatchers(HttpMethod.DELETE, "/diseases/**").hasAuthority("diseases.delete")
						.antMatchers(HttpMethod.GET, "/diseases/**").hasAnyAuthority("diseases.read")
						// user setting
						.antMatchers(HttpMethod.POST, "/users/settings/**").hasAuthority("usersetting.create")
						.antMatchers(HttpMethod.PUT, "/users/settings/**").hasAuthority("usersetting.update")
						.antMatchers(HttpMethod.DELETE, "/users/settings/**").hasAuthority("usersetting.delete")
						.antMatchers(HttpMethod.GET, "/users/settings/**").hasAuthority("usersetting.read")
						.antMatchers(HttpMethod.GET, "/users/{userName}/settings/{configName}").hasAuthority("usersetting.read")
						// user
						.antMatchers(HttpMethod.POST, "/users").hasAuthority("user.create")
						.antMatchers(HttpMethod.PUT, "/users").hasAuthority("user.update")
						.antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("user.delete")
						.antMatchers(HttpMethod.GET, "/users/**").hasAuthority("user.read")
						// permission
						.antMatchers(HttpMethod.GET, "/permissions/**").hasAuthority("permission.read")
						.antMatchers(HttpMethod.POST, "/permissions/**").hasAuthority("permission.create")
						.antMatchers(HttpMethod.PUT, "/permissions/**").hasAuthority("permission.update")
						.antMatchers(HttpMethod.DELETE, "/permissions/**").hasAuthority("permission.delete")
						// grouppermission
						.antMatchers(HttpMethod.GET, "/users/groups/**").hasAuthority("grouppermission.read")
						.antMatchers(HttpMethod.POST, "/users/groups").hasAuthority("grouppermission.create")
						.antMatchers(HttpMethod.PUT, "/users/groups").hasAuthority("grouppermission.update")
						.antMatchers(HttpMethod.DELETE, "/users/groups/**").hasAuthority("grouppermission.delete")
						
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
