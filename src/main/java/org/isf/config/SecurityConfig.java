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
package org.isf.config;

import java.util.Arrays;

import org.isf.permissions.manager.PermissionManager;
import org.isf.security.ApiAuditorAwareImpl;
import org.isf.security.CustomLogoutHandler;
import org.isf.security.OHSimpleUrlAuthenticationSuccessHandler;
import org.isf.security.RestAuthenticationEntryPoint;
import org.isf.security.jwt.JWTFilter;
import org.isf.security.jwt.TokenProvider;
import org.isf.utils.db.AuditorAwareInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
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

	@Value("${cors.allowed.origins}")
	private String allowedOrigins;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// Split the allowed origins string and add them to the configuration
		for (String origin : allowedOrigins.split(",")) {
			configuration.addAllowedOriginPattern(origin.trim());
		}
		configuration.setAllowedMethods(Arrays.asList("*")); // Allows all HTTP methods
		configuration.addAllowedHeader("*"); // Allows all headers
		configuration.setAllowCredentials(true); // Allows credentials (e.g., cookies)
		configuration.setMaxAge(3600L); // Cache preflight responses for 1 hour

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Apply configuration to all paths
		return source;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.disable()) // Disable CSRF protection
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/login", "/auth/refresh-token").permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
				// admissions
				.requestMatchers(HttpMethod.POST, "/admissions/**").hasAuthority("admissions.create")
				.requestMatchers(HttpMethod.GET, "/admissions/**").hasAnyAuthority("admissions.read")
				.requestMatchers(HttpMethod.PUT, "/admissions/**").hasAuthority("admissions.update")
				.requestMatchers(HttpMethod.DELETE, "/admissions/**").hasAuthority("admissions.delete")
				// admissiontypes
				.requestMatchers(HttpMethod.POST, "/admissiontypes/**").hasAuthority("admissiontypes.create")
				.requestMatchers(HttpMethod.GET, "/admissiontypes/**").hasAnyAuthority("admissiontypes.read")
				.requestMatchers(HttpMethod.PUT, "/admissiontypes/**").hasAuthority("admissiontypes.update")
				.requestMatchers(HttpMethod.DELETE, "/admissiontypes/**").hasAuthority("admissiontypes.delete")
				// age types
				.requestMatchers(HttpMethod.GET, "/agetypes/**").hasAnyAuthority("agetypes.read")
				.requestMatchers(HttpMethod.PUT, "/agetypes/**").hasAuthority("agetypes.update")
				// dischargetypes
				.requestMatchers(HttpMethod.POST, "/dischargetypes/**").hasAuthority("dischargetypes.create")
				.requestMatchers(HttpMethod.GET, "/dischargetypes/**").hasAnyAuthority("dischargetypes.read")
				.requestMatchers(HttpMethod.PUT, "/dischargetypes/**").hasAuthority("dischargetypes.update")
				.requestMatchers(HttpMethod.DELETE, "/dischargetypes/**").hasAuthority("dischargetypes.delete")
				// diseases
				.requestMatchers(HttpMethod.POST, "/diseases/**").hasAuthority("diseases.create")
				.requestMatchers(HttpMethod.GET, "/diseases/**").hasAnyAuthority("diseases.read")
				.requestMatchers(HttpMethod.PUT, "/diseases/**").hasAuthority("diseases.update")
				.requestMatchers(HttpMethod.DELETE, "/diseases/**").hasAuthority("diseases.delete")
				// diseasetypes
				.requestMatchers(HttpMethod.POST, "/diseasetypes/**").hasAuthority("diseasetypes.create")
				.requestMatchers(HttpMethod.GET, "/diseasetypes/**").hasAnyAuthority("diseasetypes.read")
				.requestMatchers(HttpMethod.PUT, "/diseasetypes/**").hasAuthority("diseasetypes.update")
				.requestMatchers(HttpMethod.DELETE, "/diseasetypes/**").hasAuthority("diseasetypes.delete")
				// deliveryresulttype
				.requestMatchers(HttpMethod.POST, "/deliveryresulttypes/**").hasAuthority("deliveryresulttypes.create")
				.requestMatchers(HttpMethod.GET, "/deliveryresulttypes/**").hasAnyAuthority("deliveryresulttypes.read")
				.requestMatchers(HttpMethod.PUT, "/deliveryresulttypes/**").hasAuthority("deliveryresulttypes.update")
				.requestMatchers(HttpMethod.DELETE, "/deliveryresulttypes/**").hasAuthority("deliveryresulttypes.delete")
				// deliverytypes
				.requestMatchers(HttpMethod.POST, "/deliverytypes/**").hasAuthority("deliverytypes.create")
				.requestMatchers(HttpMethod.GET, "/deliverytypes/**").hasAnyAuthority("deliverytypes.read")
				.requestMatchers(HttpMethod.PUT, "/deliverytypes/**").hasAuthority("deliverytypes.update")
				.requestMatchers(HttpMethod.DELETE, "/deliverytypes/**").hasAuthority("deliverytypes.delete")
				// exams
				.requestMatchers(HttpMethod.POST, "/exams/**").hasAuthority("exams.create")
				.requestMatchers(HttpMethod.GET, "/exams/**").hasAnyAuthority("exams.read")
				.requestMatchers(HttpMethod.PUT, "/exams/**").hasAuthority("exams.update")
				.requestMatchers(HttpMethod.DELETE, "/exams/**").hasAuthority("exams.delete")
				// examrows
				.requestMatchers(HttpMethod.POST, "/examrows/**").hasAuthority("examrows.create")
				.requestMatchers(HttpMethod.GET, "/examrows/**").hasAnyAuthority("examrows.read")
				.requestMatchers(HttpMethod.PUT, "/examrows/**").hasAuthority("examrows.update")
				.requestMatchers(HttpMethod.DELETE, "/examrows/**").hasAuthority("examrows.delete")
				// examinations
				.requestMatchers(HttpMethod.POST, "/examinations/**").hasAuthority("examinations.create")
				.requestMatchers(HttpMethod.GET, "/examinations/**").hasAnyAuthority("examinations.read")
				.requestMatchers(HttpMethod.PUT, "/examinations/**").hasAuthority("examinations.update")
				.requestMatchers(HttpMethod.DELETE, "/examinations/**").hasAuthority("examinations.delete")
				// examtypes
				.requestMatchers(HttpMethod.POST, "/examtypes/**").hasAuthority("examtypes.create")
				.requestMatchers(HttpMethod.GET, "/examtypes/**").hasAnyAuthority("examtypes.read")
				.requestMatchers(HttpMethod.PUT, "/examtypes/**").hasAuthority("examtypes.update")
				.requestMatchers(HttpMethod.DELETE, "/examtypes/**").hasAuthority("examtypes.delete")
				// hospitals
				.requestMatchers(HttpMethod.POST, "/hospitals/**").hasAuthority("hospitals.create")
				.requestMatchers(HttpMethod.GET, "/hospitals/**").permitAll()
				.requestMatchers(HttpMethod.PUT, "/hospitals/**").hasAuthority("hospitals.update")
				.requestMatchers(HttpMethod.DELETE, "/hospitals/**").hasAuthority("hospitals.delete")
				// laboratories
				.requestMatchers(HttpMethod.POST, "/laboratories/**").hasAuthority("laboratories.create")
				.requestMatchers(HttpMethod.GET, "/laboratories/**").hasAnyAuthority("laboratories.read")
				.requestMatchers(HttpMethod.PUT, "/laboratories/**").hasAuthority("laboratories.update")
				.requestMatchers(HttpMethod.DELETE, "/laboratories/**").hasAuthority("laboratories.delete")
				// malnutrition
				.requestMatchers(HttpMethod.POST, "/malnutritions/**").hasAuthority("malnutritions.create")
				.requestMatchers(HttpMethod.GET, "/malnutritions/**").hasAuthority("malnutritions.read")
				.requestMatchers(HttpMethod.PUT, "/malnutritions/**").hasAuthority("malnutritions.update")
				.requestMatchers(HttpMethod.DELETE, "/malnutritions/**").hasAuthority("malnutritions.delete")
				// medicals
				.requestMatchers(HttpMethod.POST, "/medicals/**").hasAuthority("medicals.create")
				.requestMatchers(HttpMethod.GET, "/medicals/**").hasAuthority("medicals.read")
				.requestMatchers(HttpMethod.PUT, "/medicals/**").hasAuthority("medicals.update")
				.requestMatchers(HttpMethod.DELETE, "/medicals/**").hasAuthority("medicals.delete")
				// medicalstock
				.requestMatchers(HttpMethod.POST, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.create")
				.requestMatchers(HttpMethod.GET, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.read")
				.requestMatchers(HttpMethod.PUT, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.update")
				.requestMatchers(HttpMethod.DELETE, "/medicalstockmovements/**").hasAuthority("medicalstockmovements.delete")
				// medicalstockward
				.requestMatchers(HttpMethod.POST, "/medicalstockward/**").hasAuthority("medicalstockward.create")
				.requestMatchers(HttpMethod.GET, "/medicalstockward/**").hasAuthority("medicalstockward.read")
				.requestMatchers(HttpMethod.PUT, "/medicalstockward/**").hasAuthority("medicalstockward.update")
				.requestMatchers(HttpMethod.DELETE, "/medicalstockward/**").hasAuthority("medicalstockward.delete")
				// medicalstockmovtype
				.requestMatchers(HttpMethod.POST, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.create")
				.requestMatchers(HttpMethod.GET, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.read")
				.requestMatchers(HttpMethod.PUT, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.update")
				.requestMatchers(HttpMethod.DELETE, "/medstockmovementtypes/**").hasAuthority("medstockmovementtypes.delete")
				// medicaltype
				.requestMatchers(HttpMethod.POST, "/medicaltypes/**").hasAuthority("medicaltypes.create")
				.requestMatchers(HttpMethod.GET, "/medicaltypes/**").hasAuthority("medicaltypes.read")
				.requestMatchers(HttpMethod.PUT, "/medicaltypes/**").hasAuthority("medicaltypes.update")
				.requestMatchers(HttpMethod.DELETE, "/medicaltypes/**").hasAuthority("medicaltypes.delete")
				// opd
				.requestMatchers(HttpMethod.POST, "/opds/**").hasAuthority("opds.create")
				.requestMatchers(HttpMethod.GET, "/opds/**").hasAnyAuthority("opds.read")
				.requestMatchers(HttpMethod.PUT, "/opds/**").hasAuthority("opds.update")
				.requestMatchers(HttpMethod.DELETE, "/opds/**").hasAuthority("opds.delete")
				// operations
				.requestMatchers(HttpMethod.POST, "/operations/**").hasAuthority("operations.create")
				.requestMatchers(HttpMethod.GET, "/operations/**").hasAnyAuthority("operations.read")
				.requestMatchers(HttpMethod.PUT, "/operations/**").hasAuthority("operations.update")
				.requestMatchers(HttpMethod.DELETE, "/operations/**").hasAuthority("operations.delete")
				// operation types
				.requestMatchers(HttpMethod.POST, "/operationtypes/**").hasAuthority("operationtypes.create")
				.requestMatchers(HttpMethod.GET, "/operationtypes/**").hasAnyAuthority("operationtypes.read")
				.requestMatchers(HttpMethod.PUT, "/operationtypes/**").hasAuthority("operationtypes.update")
				.requestMatchers(HttpMethod.DELETE, "/operationtypes/**").hasAuthority("operationtypes.delete")
				// patientconsensus
				.requestMatchers(HttpMethod.POST, "/patientconsensus/**").hasAuthority("patientconsensus.create")
				.requestMatchers(HttpMethod.GET, "/patientconsensus/**").hasAuthority("patientconsensus.read")
				.requestMatchers(HttpMethod.PUT, "/patientconsensus/**").hasAuthority("patientconsensus.update")
				.requestMatchers(HttpMethod.DELETE, "/patientconsensus/**").hasAuthority("patientconsensus.delete")
				// patients
				.requestMatchers(HttpMethod.POST, "/patients/**").hasAuthority("patients.create")
				.requestMatchers(HttpMethod.GET, "/patients/**").hasAuthority("patients.read")
				.requestMatchers(HttpMethod.PUT, "/patients/**").hasAuthority("patients.update")
				.requestMatchers(HttpMethod.DELETE, "/patients/**").hasAuthority("patients.delete")
				// patientvaccines
				.requestMatchers(HttpMethod.POST, "/patientvaccines/**").hasAuthority("patientvaccines.create")
				.requestMatchers(HttpMethod.GET, "/patientvaccines/**").hasAnyAuthority("patientvaccines.read")
				.requestMatchers(HttpMethod.PUT, "/patientvaccines/**").hasAuthority("patientvaccines.update")
				.requestMatchers(HttpMethod.DELETE, "/patientvaccines/**").hasAuthority("patientvaccines.delete")
				// permission
				.requestMatchers(HttpMethod.POST, "/permissions/**").hasAuthority("permissions.create")
				.requestMatchers(HttpMethod.GET, "/permissions/**").hasAuthority("permissions.read")
				.requestMatchers(HttpMethod.PUT, "/permissions/**").hasAuthority("permissions.update")
				.requestMatchers(HttpMethod.DELETE, "/permissions/**").hasAuthority("permissions.delete")
				// grouppermission
				.requestMatchers(HttpMethod.POST, "/usergroups/{group_code}/permissions/**").hasAuthority("grouppermission.create")
				.requestMatchers(HttpMethod.GET, "/usergroups/{group_code}/permissions/**").hasAuthority("grouppermission.read")
				.requestMatchers(HttpMethod.PUT, "/usergroups/{group_code}/permissions/**").hasAuthority("grouppermission.create")
				.requestMatchers(HttpMethod.PATCH, "/usergroups/{group_code}/permissions/**")
				.access((authentication, context) -> {
					boolean hasCreateAuthority = authentication.get().getAuthorities().stream()
						.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("grouppermission.create"));
					boolean hasDeleteAuthority = authentication.get().getAuthorities().stream()
						.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("grouppermission.delete"));

					return new AuthorizationDecision(hasCreateAuthority && hasDeleteAuthority);
				})
				.requestMatchers(HttpMethod.DELETE, "/usergroups/{group_code}/permissions/**").hasAuthority("grouppermission.delete")
				// usergroups
				.requestMatchers(HttpMethod.POST, "/usergroups/**").hasAuthority("usergroups.create")
				.requestMatchers(HttpMethod.GET, "/usergroups/**").hasAuthority("usergroups.read")
				.requestMatchers(HttpMethod.PUT, "/usergroups/**").hasAuthority("usergroups.update")
				.requestMatchers(HttpMethod.DELETE, "/usergroups/**").hasAuthority("usergroups.delete")
				// user
				.requestMatchers("/users/me").authenticated()
				.requestMatchers(HttpMethod.POST, "/users").hasAuthority("users.create")
				.requestMatchers(HttpMethod.GET, "/users/**").hasAuthority("users.read")
				.requestMatchers(HttpMethod.PUT, "/users/{username}").hasAuthority("users.update")
				.requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("users.delete")
				// user setting
				.requestMatchers("/usersettings/**").authenticated()
				// pregnanttreatmenttypes
				.requestMatchers(HttpMethod.POST, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.create")
				.requestMatchers(HttpMethod.GET, "/pregnanttreatmenttypes/**").hasAnyAuthority("pregnanttreatmenttypes.read")
				.requestMatchers(HttpMethod.PUT, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.update")
				.requestMatchers(HttpMethod.DELETE, "/pregnanttreatmenttypes/**").hasAuthority("pregnanttreatmenttypes.delete")
				// pricelists
				.requestMatchers(HttpMethod.POST, "/pricelists/**").hasAuthority("pricelists.create")
				.requestMatchers(HttpMethod.GET, "/pricelists/**").hasAnyAuthority("pricelists.read")
				.requestMatchers(HttpMethod.PUT, "/pricelists/**").hasAuthority("pricelists.update")
				.requestMatchers(HttpMethod.DELETE, "/pricelists/**").hasAuthority("pricelists.delete")
				// pricesothers
				.requestMatchers(HttpMethod.POST, "/pricesothers/**").hasAuthority("pricesothers.create")
				.requestMatchers(HttpMethod.GET, "/pricesothers/**").hasAnyAuthority("pricesothers.read")
				.requestMatchers(HttpMethod.PUT, "/pricesothers/**").hasAuthority("pricesothers.update")
				.requestMatchers(HttpMethod.DELETE, "/pricesothers/**").hasAuthority("pricesothers.delete")
				// reports
				.requestMatchers(HttpMethod.POST, "/reports/**").hasAuthority("reports.create")
				.requestMatchers(HttpMethod.GET, "/reports/**").hasAnyAuthority("reports.read")
				.requestMatchers(HttpMethod.PUT, "/reports/**").hasAuthority("reports.update")
				.requestMatchers(HttpMethod.DELETE, "/reports/**").hasAuthority("reports.delete")
				// sms
				.requestMatchers(HttpMethod.POST, "/sms/**").hasAuthority("sms.create")
				.requestMatchers(HttpMethod.GET, "/sms/**").hasAnyAuthority("sms.read")
				.requestMatchers(HttpMethod.PUT, "/sms/**").hasAuthority("sms.update")
				.requestMatchers(HttpMethod.DELETE, "/sms/**").hasAuthority("sms.delete")
				// suppliers
				.requestMatchers(HttpMethod.POST, "/suppliers/**").hasAuthority("suppliers.create")
				.requestMatchers(HttpMethod.GET, "/suppliers/**").hasAnyAuthority("suppliers.read")
				.requestMatchers(HttpMethod.PUT, "/suppliers/**").hasAuthority("suppliers.update")
				.requestMatchers(HttpMethod.DELETE, "/suppliers/**").hasAuthority("suppliers.delete")
				// therapy
				.requestMatchers(HttpMethod.POST, "/therapies/**").hasAuthority("therapies.create")
				.requestMatchers(HttpMethod.GET, "/therapies/**").hasAnyAuthority("therapies.read")
				.requestMatchers(HttpMethod.PUT, "/therapies/**").hasAuthority("therapies.update")
				.requestMatchers(HttpMethod.DELETE, "/therapies/**").hasAuthority("therapies.delete")
				// vaccines
				.requestMatchers(HttpMethod.POST, "/vaccines/**").hasAuthority("vaccines.create")
				.requestMatchers(HttpMethod.GET, "/vaccines/**").hasAnyAuthority("vaccines.read")
				.requestMatchers(HttpMethod.PUT, "/vaccines/**").hasAuthority("vaccines.update")
				.requestMatchers(HttpMethod.DELETE, "/vaccines/**").hasAuthority("vaccines.delete")
				// vaccineType
				.requestMatchers(HttpMethod.POST, "/vaccinetypes/**").hasAuthority("vaccinetypes.create")
				.requestMatchers(HttpMethod.GET, "/vaccinetypes/**").hasAnyAuthority("vaccinetypes.read")
				.requestMatchers(HttpMethod.PUT, "/vaccinetypes/**").hasAuthority("vaccinetypes.update")
				.requestMatchers(HttpMethod.DELETE, "/vaccinetypes/**").hasAuthority("vaccinetypes.delete")
				// visit
				.requestMatchers(HttpMethod.POST, "/visits/**").hasAuthority("visits.create")
				.requestMatchers(HttpMethod.GET, "/visits/**").hasAnyAuthority("visits.read")
				.requestMatchers(HttpMethod.PUT, "/visits/**").hasAuthority("visits.update")
				.requestMatchers(HttpMethod.DELETE, "/visits/**").hasAuthority("visits.delete")
				// wards
				.requestMatchers(HttpMethod.POST, "/wards/**").hasAuthority("wards.create")
				.requestMatchers(HttpMethod.GET, "/wards/**").hasAnyAuthority("wards.read")
				.requestMatchers(HttpMethod.PUT, "/wards/**").hasAuthority("wards.update")
				.requestMatchers(HttpMethod.DELETE, "/wards/**").hasAuthority("wards.delete")

				.anyRequest().authenticated()

			)
			.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint))
			.logout(logout -> logout.logoutUrl("/auth/logout")
				.addLogoutHandler(customLogoutHandler)
				.permitAll())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(new JWTFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public SimpleUrlAuthenticationFailureHandler failureHandler() {
		return new SimpleUrlAuthenticationFailureHandler();
	}

	@Bean
	public SimpleUrlAuthenticationSuccessHandler successHandler() {
		return new OHSimpleUrlAuthenticationSuccessHandler(tokenProvider);
	}

	@Bean
	public AuditorAwareInterface auditorAwareCustomizer() {
		return new ApiAuditorAwareImpl();
	}
}
