package org.isf.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageBundleConfig {

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:language");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds((int) TimeUnit.HOURS.toSeconds(1));
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}
}
