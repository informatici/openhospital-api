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
	
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
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
            //.antMatchers("/auth-needed/**").authenticated()
            //.antMatchers("/noauth-public/**").permitAll()
            //.antMatchers("/admin/**").hasAuthority("admin")
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
