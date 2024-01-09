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
import org.isf.security.ApiAuditorAwareImpl;
import org.isf.security.OHSimpleUrlAuthenticationSuccessHandler;
import org.isf.security.RestAuthenticationEntryPoint;
import org.isf.security.jwt.JWTConfigurer;
import org.isf.security.jwt.TokenProvider;
import org.isf.utils.db.AuditorAwareInterface;
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

						// admissions
						.antMatchers(HttpMethod.POST, "/admissions/**").hasAuthority("admissions.create")
						.antMatchers(HttpMethod.GET, "/admissions/**").hasAnyAuthority("admissions.read")
						.antMatchers(HttpMethod.PUT, "/admissions/**").hasAuthority("admissions.update")
						.antMatchers(HttpMethod.DELETE, "/admissions/**").hasAuthority("admissions.delete")
						// admissiontypes
						.antMatchers(HttpMethod.POST, "/admissiontypes/**").hasAuthority("admissiontypes.create")
						.antMatchers(HttpMethod.GET, "/admissiontypes/**").hasAnyAuthority("admissiontypes.read")
						.antMatchers(HttpMethod.PUT, "/admissiontypes/**").hasAuthority("admissiontypes.update")
						.antMatchers(HttpMethod.DELETE, "/admissiontypes/**").hasAuthority("admissiontypes.delete")
						// age types
						.antMatchers(HttpMethod.GET, "/agetypes/**").hasAnyAuthority("agetypes.read")
						.antMatchers(HttpMethod.PUT, "/agetypes/**").hasAuthority("agetypes.update")
						// dischargetypes
						.antMatchers(HttpMethod.POST, "/dischargetypes/**").hasAuthority("dischargetypes.create")
						.antMatchers(HttpMethod.GET, "/dischargetypes/**").hasAnyAuthority("dischargetypes.read")
						.antMatchers(HttpMethod.PUT, "/dischargetypes/**").hasAuthority("dischargetypes.update")
						.antMatchers(HttpMethod.DELETE, "/dischargetypes/**").hasAuthority("dischargetypes.delete")
						// diseases
						.antMatchers(HttpMethod.POST, "/diseases/**").hasAuthority("diseases.create")
						.antMatchers(HttpMethod.GET, "/diseases/**").hasAnyAuthority("diseases.read")
						.antMatchers(HttpMethod.PUT, "/diseases/**").hasAuthority("diseases.update")
						.antMatchers(HttpMethod.DELETE, "/diseases/**").hasAuthority("diseases.delete")
						// diseasetypes
						.antMatchers(HttpMethod.POST, "/diseasetypes/**").hasAuthority("diseasetypes.create")
						.antMatchers(HttpMethod.GET, "/diseasetypes/**").hasAnyAuthority("diseasetypes.read")
						.antMatchers(HttpMethod.PUT, "/diseasetypes/**").hasAuthority("diseasetypes.update")
						.antMatchers(HttpMethod.DELETE, "/diseasetypes/**").hasAuthority("diseasetypes.delete")
						// deliveryresulttype
						.antMatchers(HttpMethod.POST, "/deliveryresulttypes/**").hasAuthority("deliveryresulttypes.create")
						.antMatchers(HttpMethod.GET, "/deliveryresulttypes/**").hasAnyAuthority("deliveryresulttypes.read")
						.antMatchers(HttpMethod.PUT, "/deliveryresulttypes/**").hasAuthority("deliveryresulttypes.update")
						.antMatchers(HttpMethod.DELETE, "/deliveryresulttypes/**").hasAuthority("deliveryresulttypes.delete")
						// deliverytypes
						.antMatchers(HttpMethod.POST, "/deliverytypes/**").hasAuthority("deliverytypes.create")
						.antMatchers(HttpMethod.GET, "/deliverytypes/**").hasAnyAuthority("deliverytypes.read")
						.antMatchers(HttpMethod.PUT, "/deliverytypes/**").hasAuthority("deliverytypes.update")
						.antMatchers(HttpMethod.DELETE, "/deliverytypes/**").hasAuthority("deliverytypes.delete")
						// exams
						.antMatchers(HttpMethod.POST, "/exams/**").hasAuthority("exams.create")
						.antMatchers(HttpMethod.GET, "/exams/**").hasAnyAuthority("exams.read")
						.antMatchers(HttpMethod.PUT, "/exams/**").hasAuthority("exams.update")
						.antMatchers(HttpMethod.DELETE, "/exams/**").hasAuthority("exams.delete")
						// examrows
						.antMatchers(HttpMethod.POST, "/examrows/**").hasAuthority("examrows.create")
						.antMatchers(HttpMethod.GET, "/examrows/**").hasAnyAuthority("examrows.read")
						.antMatchers(HttpMethod.PUT, "/examrows/**").hasAuthority("examrows.update")
						.antMatchers(HttpMethod.DELETE, "/examrows/**").hasAuthority("examrows.delete")
						// examinations
						.antMatchers(HttpMethod.POST, "/examinations/**").hasAuthority("examinations.create")
						.antMatchers(HttpMethod.GET, "/examinations/**").hasAnyAuthority("examinations.read")
						.antMatchers(HttpMethod.PUT, "/examinations/**").hasAuthority("examinations.update")
						.antMatchers(HttpMethod.DELETE, "/examinations/**").hasAuthority("examinations.delete")
						// examtypes
						.antMatchers(HttpMethod.POST, "/examtypes/**").hasAuthority("examtypes.create")
						.antMatchers(HttpMethod.GET, "/examtypes/**").hasAnyAuthority("examtypes.read")
						.antMatchers(HttpMethod.PUT, "/examtypes/**").hasAuthority("examtypes.update")
						.antMatchers(HttpMethod.DELETE, "/examtypes/**").hasAuthority("examtypes.delete")
						// hospitals
						.antMatchers(HttpMethod.POST, "/hospitals/**").hasAuthority("hospitals.create")
						// .antMatchers(HttpMethod.GET, "/hospitals/**").hasAnyAuthority("hospital.read") to anyone
						.antMatchers(HttpMethod.PUT, "/hospitals/**").hasAuthority("hospitals.update")
						.antMatchers(HttpMethod.DELETE, "/hospitals/**").hasAuthority("hospitals.delete")
						// laboratories
						.antMatchers(HttpMethod.POST, "/laboratories/**").hasAuthority("laboratories.create")
						.antMatchers(HttpMethod.GET, "/laboratories/**").hasAnyAuthority("laboratories.read")
						.antMatchers(HttpMethod.PUT, "/laboratories/**").hasAuthority("laboratories.update")
						.antMatchers(HttpMethod.DELETE, "/laboratories/**").hasAuthority("laboratories.delete")
						// malnutrition
						.antMatchers(HttpMethod.POST, "/malnutritions/**").hasAuthority("malnutritions.create")
						.antMatchers(HttpMethod.GET, "/malnutritions/**").hasAuthority("malnutritions.read")
						.antMatchers(HttpMethod.PUT, "/malnutritions/**").hasAuthority("malnutritions.update")
						.antMatchers(HttpMethod.DELETE, "/malnutritions/**").hasAuthority("malnutritions.delete")
						// medicals
						.antMatchers(HttpMethod.POST, "/medicals/**").hasAuthority("medicals.create")
						.antMatchers(HttpMethod.GET, "/medicals/**").hasAuthority("medicals.read")
						.antMatchers(HttpMethod.PUT, "/medicals/**").hasAuthority("medicals.update")
						.antMatchers(HttpMethod.DELETE, "/medicals/**").hasAuthority("medicals.delete")
						// medicalstock
						.antMatchers(HttpMethod.POST, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.create")
						.antMatchers(HttpMethod.GET, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.read")
						.antMatchers(HttpMethod.PUT, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.update")
						.antMatchers(HttpMethod.DELETE, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.delete")
						// medicalstockward
						.antMatchers(HttpMethod.POST, "/medicalstockward/**").hasAuthority("medicalstockward.create")
						.antMatchers(HttpMethod.GET, "/medicalstockward/**").hasAuthority("medicalstockward.read")
						.antMatchers(HttpMethod.PUT, "/medicalstockward/**").hasAuthority("medicalstockward.update")
						.antMatchers(HttpMethod.DELETE, "/medicalstockward/**").hasAuthority("medicalstockward.delete")
						// medicalstockmovtype
						.antMatchers(HttpMethod.POST, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.create")
						.antMatchers(HttpMethod.GET, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.read")
						.antMatchers(HttpMethod.PUT, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.update")
						.antMatchers(HttpMethod.DELETE, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.delete")
						// medicaltype
						.antMatchers(HttpMethod.POST, "/medicaltypes/**").hasAuthority("medicaltypes.create")
						.antMatchers(HttpMethod.GET, "/medicaltypes/**").hasAuthority("medicaltypes.read")
						.antMatchers(HttpMethod.PUT, "/medicaltypes/**").hasAuthority("medicaltypes.update")
						.antMatchers(HttpMethod.DELETE, "/medicaltypes/**").hasAuthority("medicaltypes.delete")
						// opd
						.antMatchers(HttpMethod.POST, "/opds/**").hasAuthority("opds.create")
						.antMatchers(HttpMethod.GET, "/opds/**").hasAnyAuthority("opds.read")
						.antMatchers(HttpMethod.PUT, "/opds/**").hasAuthority("opds.update")
						.antMatchers(HttpMethod.DELETE, "/opds/**").hasAuthority("opds.delete")
						// operations
						.antMatchers(HttpMethod.POST, "/operations/**").hasAuthority("operations.create")
						.antMatchers(HttpMethod.GET, "/operations/**").hasAnyAuthority("operations.read")
						.antMatchers(HttpMethod.PUT, "/operations/**").hasAuthority("operations.update")
						.antMatchers(HttpMethod.DELETE, "/operations/**").hasAuthority("operations.delete")
						// operation types
						.antMatchers(HttpMethod.POST, "/operationtypes/**").hasAuthority("operationtypes.create")
						.antMatchers(HttpMethod.GET, "/operationtypes/**").hasAnyAuthority("operationtypes.read")
						.antMatchers(HttpMethod.PUT, "/operationtypes/**").hasAuthority("operationtypes.update")
						.antMatchers(HttpMethod.DELETE, "/operationtypes/**").hasAuthority("operationtypes.delete")
						// patientconsensus
						.antMatchers(HttpMethod.POST, "/patientconsensus/**").hasAuthority("patientconsensus.create")
						.antMatchers(HttpMethod.GET, "/patientconsensus/**").hasAuthority("patientconsensus.read")
						.antMatchers(HttpMethod.PUT, "/patientconsensus/**").hasAuthority("patientconsensus.update")
						.antMatchers(HttpMethod.DELETE, "/patientconsensus/**").hasAuthority("patientconsensus.delete")
						// patients
						.antMatchers(HttpMethod.POST, "/patients/**").hasAuthority("patients.create")
						.antMatchers(HttpMethod.GET, "/patients/**").hasAuthority("patients.read")
						.antMatchers(HttpMethod.PUT, "/patients/**").hasAuthority("patients.update")
						.antMatchers(HttpMethod.DELETE, "/patients/**").hasAuthority("patients.delete")
						// patientvaccines
						.antMatchers(HttpMethod.POST, "/patientvaccines/**").hasAuthority("patientvaccines.create")
						.antMatchers(HttpMethod.GET, "/patientvaccines/**").hasAnyAuthority("patientvaccines.read")
						.antMatchers(HttpMethod.PUT, "/patientvaccines/**").hasAuthority("patientvaccines.update")
						.antMatchers(HttpMethod.DELETE, "/patientvaccines/**").hasAuthority("patientvaccines.delete")
						// permission
						.antMatchers(HttpMethod.POST, "/permissions/**").hasAuthority("permissions.create")
						.antMatchers(HttpMethod.GET, "/permissions/**").hasAuthority("permissions.read")
						.antMatchers(HttpMethod.PUT, "/permissions/**").hasAuthority("permissions.update")
						.antMatchers(HttpMethod.DELETE, "/permissions/**").hasAuthority("permissions.delete")
						// grouppermission
						.antMatchers(HttpMethod.POST, "/users/groups").hasAuthority("grouppermission.create")
						.antMatchers(HttpMethod.GET, "/users/groups/**").hasAuthority("grouppermission.read")
						.antMatchers(HttpMethod.PUT, "/users/groups").hasAuthority("grouppermission.update")
						.antMatchers(HttpMethod.DELETE, "/users/groups/**").hasAuthority("grouppermission.delete")
						// user
						.antMatchers(HttpMethod.POST, "/users").hasAuthority("users.create")
						.antMatchers(HttpMethod.GET, "/users/**").hasAuthority("users.read")
						.antMatchers(HttpMethod.PUT, "/users").hasAuthority("users.update")
						.antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("users.delete")
						// user setting
						.antMatchers(HttpMethod.GET, "/users/settings/**").hasAuthority("usersettings.read")
						.antMatchers(HttpMethod.GET, "/users/{userName}/settings/{configName}").hasAuthority("usersettings.read")
						.antMatchers(HttpMethod.POST, "/users/settings/**").hasAuthority("usersettings.create")
						.antMatchers(HttpMethod.PUT, "/users/settings/**").hasAuthority("usersettings.update")
						.antMatchers(HttpMethod.DELETE, "/users/settings/**").hasAuthority("usersettings.delete")
						// pregnanttreatmenttypes
						.antMatchers(HttpMethod.POST, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.create")
						.antMatchers(HttpMethod.GET, "/pregnanttreatmenttypes/**").hasAnyAuthority("pregnanttreatmenttypes.read")
						.antMatchers(HttpMethod.PUT, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.update")
						.antMatchers(HttpMethod.DELETE, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.delete")
						// pricelists
						.antMatchers(HttpMethod.POST, "/pricelists/**").hasAuthority("pricelists.create")
						.antMatchers(HttpMethod.GET, "/pricelists/**").hasAnyAuthority("pricelists.read")
						.antMatchers(HttpMethod.PUT, "/pricelists/**").hasAuthority("pricelists.update")
						.antMatchers(HttpMethod.DELETE, "/pricelists/**").hasAuthority("pricelists.delete")
						// pricesothers
						.antMatchers(HttpMethod.POST, "/pricesothers/**").hasAuthority("pricesothers.create")
						.antMatchers(HttpMethod.GET, "/pricesothers/**").hasAnyAuthority("pricesothers.read")
						.antMatchers(HttpMethod.PUT, "/pricesothers/**").hasAuthority("pricesothers.update")
						.antMatchers(HttpMethod.DELETE, "/pricesothers/**").hasAuthority("pricesothers.delete")
						// reports
						.antMatchers(HttpMethod.POST, "/reports/**").hasAuthority("reports.create")
						.antMatchers(HttpMethod.GET, "/reports/**").hasAnyAuthority("reports.read")
						.antMatchers(HttpMethod.PUT, "/reports/**").hasAuthority("reports.update")
						.antMatchers(HttpMethod.DELETE, "/reports/**").hasAuthority("reports.delete")
						// sms
						.antMatchers(HttpMethod.POST, "/sms/**").hasAuthority("sms.create")
						.antMatchers(HttpMethod.GET, "/sms/**").hasAnyAuthority("sms.read")
						.antMatchers(HttpMethod.PUT, "/sms/**").hasAuthority("sms.update")
						.antMatchers(HttpMethod.DELETE, "/sms/**").hasAuthority("sms.delete")
						// suppliers
						.antMatchers(HttpMethod.POST, "/suppliers/**").hasAuthority("suppliers.create")
						.antMatchers(HttpMethod.GET, "/suppliers/**").hasAnyAuthority("suppliers.read")
						.antMatchers(HttpMethod.PUT, "/suppliers/**").hasAuthority("suppliers.update")
						.antMatchers(HttpMethod.DELETE, "/suppliers/**").hasAuthority("suppliers.delete")
						// therapy
						.antMatchers(HttpMethod.POST, "/therapies/**").hasAuthority("therapies.create")
						.antMatchers(HttpMethod.GET, "/therapies/**").hasAnyAuthority("therapies.read")
						.antMatchers(HttpMethod.PUT, "/therapies/**").hasAuthority("therapies.update")
						.antMatchers(HttpMethod.DELETE, "/therapies/**").hasAuthority("therapies.delete")
						// vaccines
						.antMatchers(HttpMethod.POST, "/vaccines/**").hasAuthority("vaccines.create")
						.antMatchers(HttpMethod.GET, "/vaccines/**").hasAnyAuthority("vaccines.read")
						.antMatchers(HttpMethod.PUT, "/vaccines/**").hasAuthority("vaccines.update")
						.antMatchers(HttpMethod.DELETE, "/vaccines/**").hasAuthority("vaccines.delete")
						// vaccineType
						.antMatchers(HttpMethod.POST, "/vaccinetypes/**").hasAuthority("vaccinetypes.create")
						.antMatchers(HttpMethod.GET, "/vaccinetypes/**").hasAnyAuthority("vaccinetypes.read")
						.antMatchers(HttpMethod.PUT, "/vaccinetypes/**").hasAuthority("vaccinetypes.update")
						.antMatchers(HttpMethod.DELETE, "/vaccinetypes/**").hasAuthority("vaccinetypes.delete")
						// visit
						.antMatchers(HttpMethod.POST, "/visits/**").hasAuthority("visits.create")
						.antMatchers(HttpMethod.GET, "/visits/**").hasAnyAuthority("visits.read")
						.antMatchers(HttpMethod.PUT, "/visits/**").hasAuthority("visits.update")
						.antMatchers(HttpMethod.DELETE, "/visits/**").hasAuthority("visits.delete")
						// wards
						.antMatchers(HttpMethod.POST, "/wards/**").hasAuthority("wards.create")
						.antMatchers(HttpMethod.GET, "/wards/**").hasAnyAuthority("wards.read")
						.antMatchers(HttpMethod.PUT, "/wards/**").hasAuthority("wards.update")
						.antMatchers(HttpMethod.DELETE, "/wards/**").hasAuthority("wards.delete")

						// .antMatchers("/auth-needed/**").authenticated()
						// .antMatchers("/noauth-public/**").permitAll()
						// .antMatchers("/admin/**").hasAuthority("admin")

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

	@Bean
	public AuditorAwareInterface auditorAwareCustomizer() {
		return new ApiAuditorAwareImpl();
	}
}
