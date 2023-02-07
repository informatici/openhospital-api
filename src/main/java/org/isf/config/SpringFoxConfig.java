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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import io.swagger.models.auth.In;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    @Autowired
    private Environment env;

    @Bean
    public Docket apiDocket() {

        List<SecurityScheme> securitySchemes = Arrays.asList(new ApiKey("JWT", "Authorization", "header"));

        ApiInfo apiInfo = new ApiInfo("OH 2.0 Api Documentation", "OH 2.0 Api Documentation", "1.0", "urn:tos", ApiInfo.DEFAULT_CONTACT, "Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0", new ArrayList());

        String host = env.getProperty("api.host");
        String protocol = env.getProperty("api.protocol");

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
                .securitySchemes(Arrays.asList(apiKey()));
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


    private SecurityReference apiKeyReference() {
        return new SecurityReference("Authorization", new AuthorizationScope[0]);
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", HttpHeaders.AUTHORIZATION, In.HEADER.name());
    }

}