package org.isf.config;

import org.isf.security.OHSimpleUrlAuthenticationSuccessHandler;
import org.isf.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
	  throws Exception {
	    auth.authenticationProvider(authenticationProvider());
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider
	      = new DaoAuthenticationProvider();
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
		// config.setAllowedHeaders(Arrays.asList("Accept", "Accept-Encoding", "Accept-Language", "Authorization", "Content-Type", "Cache-Control", "Connection", "Cookie", "Host", "Pragma", "Referer, User-Agent"));
		config.setAllowedMethods(Arrays.asList("*"));
		// config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		config.setAllowCredentials(true);
		config.setAllowedOrigins(Arrays.asList("*"));
		config.setMaxAge(3600L);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors()
			.and()
			.csrf()
				.disable()
					.authorizeRequests()
            .and()
            .exceptionHandling()
            	//.accessDeniedHandler(accessDeniedHandler)
            	.authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .authorizeRequests()
            	.antMatchers("/auth/**").permitAll()
            	.antMatchers(HttpMethod.POST, "/patients/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/patients/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/patients/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PATCH, "/patients/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/patients/**").hasAnyAuthority("admin", "guest")
            	.antMatchers(HttpMethod.POST, "/admissiontypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/admissiontypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/admissiontypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PATCH, "/admissiontypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/admissiontypes/**").hasAnyAuthority("admin", "guest")
            	.antMatchers(HttpMethod.POST, "/deliveryresulttype/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/deliveryresulttype/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/deliveryresulttype/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PATCH, "/deliveryresulttype/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/deliveryresulttype/**").hasAnyAuthority("admin", "guest")
            	.antMatchers(HttpMethod.POST, "/deliverytypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/deliverytypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/deliverytypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PATCH, "/deliverytypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/deliverytypes/**").hasAnyAuthority("admin", "guest")
            	.antMatchers(HttpMethod.POST, "/dischargetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/dischargetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/dischargetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PATCH, "/dischargetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/dischargetypes/**").hasAnyAuthority("admin", "guest")
            	.antMatchers(HttpMethod.POST, "/admissions/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/admissions/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/admissions/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PATCH, "/admissions/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/admissions/**").hasAnyAuthority("admin", "guest")
            	//.antMatchers("/auth-needed/**").authenticated()
            	//.antMatchers("/noauth-public/**").permitAll()
            	//.antMatchers("/admin/**").hasAuthority("admin")
            	//age types
            	.antMatchers(HttpMethod.PUT, "/agetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/agetypes/**").hasAnyAuthority("admin", "guest")
            	//disease types
            	.antMatchers(HttpMethod.POST, "/diseasetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/diseasetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/diseasetypes/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/diseasetypes/**").hasAnyAuthority("admin", "guest")
            	//diseases
            	.antMatchers(HttpMethod.POST, "/diseases/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.PUT, "/diseases/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.DELETE, "/diseases/**").hasAuthority("admin")
            	.antMatchers(HttpMethod.GET, "/diseases/**").hasAnyAuthority("admin", "guest")
            .and()
          	.formLogin()
          		.loginPage("/auth/login")
            		.successHandler(successHandler())
            		.failureHandler(failureHandler())
            .and()
            .httpBasic()
            .and()
          	.logout()
				.logoutUrl("/auth/logout")
          			.permitAll();
    }
    
    
    @Bean
	public SimpleUrlAuthenticationFailureHandler failureHandler() {
    	return new SimpleUrlAuthenticationFailureHandler();
    }
    
    @Bean
	public SimpleUrlAuthenticationSuccessHandler successHandler() {
    	return new OHSimpleUrlAuthenticationSuccessHandler();
    }
}
