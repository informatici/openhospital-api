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

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecuritySchemes({
	@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    bearerFormat = "JWT",
	    scheme = "bearer"
	)
})
public class OpenAPIConfiguration {

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI().addSecurityItem(
			new SecurityRequirement().addList("bearerAuth"))
								     .info(new Info().title("OH 2.0 Api Documentation")
								     .description("OH 2.0 Api Documentation")
								     .version("1.0").contact(new Contact().name("ApiInfo.DEFAULT_CONTACT"))
								     .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
								     .servers(List.of(new Server().url("http://localhost:8080")))
								     .components(new Components().schemas(Map.of(
								    		 "LocalDate", new DateSchema().name("LocalDate").type("string").format(null),
								    		 "LocalDateTime", new DateTimeSchema().name("LocalDateTime").type("string").format(null))));
	}
}