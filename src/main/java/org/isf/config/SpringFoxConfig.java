package org.isf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {
    @Bean
    public Docket apiDocket() {

        List<SecurityScheme> securitySchemes = Arrays.asList(new ApiKey("JWT", "Authorization", "header"));

        ApiInfo apiInfo = new ApiInfo("OH 2.0 Api Documentation", "OH 2.0 Api Documentation", "1.0", "urn:tos", ApiInfo.DEFAULT_CONTACT, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList());

        String host = System.getProperty("api.host", "localhost:8080");
        String protocol = System.getProperty("api.protocol", "http");

        Set<String> protocols = new HashSet<>();
        protocols.add(protocol);


        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .host(host)
                .protocols(protocols)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.isf"))
                //.apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Arrays.asList(jwtSecurityContext()))
                .securitySchemes(securitySchemes);
    }

    private SecurityContext jwtSecurityContext() {
        return SecurityContext
                .builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!(\\/auth\\/login)).*$"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        return Arrays.asList(new SecurityReference("JWT", new AuthorizationScope[0]));
    }

}