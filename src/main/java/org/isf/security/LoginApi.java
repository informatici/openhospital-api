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
package org.isf.security;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Here only for swagger
 *
 * @author antonio
 */

@RestController
@OpenAPIDefinition(info = @Info(title = "Login API", version = "1.0.0"))
public class LoginApi {
    /**
     * Implemented by Spring Security
     */
    @Operation(method = "Login", description = "Login with the given credentials.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @PostMapping(value = "/auth/login")
    void login(@Valid @RequestParam String username,  @RequestParam String password) {
    	 throw new IllegalStateException("Add Spring Security to handle authentication");
    }

    /**
     * Implemented by Spring Security
     */
    @Operation(method = "Logout", description = "Logout the current user.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @PostMapping(value = "/auth/logout")
    void logout() {
        throw new IllegalStateException("Add Spring Security to handle authentication");
    }
}
