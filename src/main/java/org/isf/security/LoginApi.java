package org.isf.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Here only for swagger
 *
 * @author antonio
 */
@Api("Login")
@RestController
public class LoginApi {
    /**
     * Implemented by Spring Security
     */
    @ApiOperation(value = "Login", notes = "Login with the given credentials.")
    @ApiResponses({@ApiResponse(code = 200, message = "", response = Authentication.class)})
    @PostMapping(value = "/auth/login")
    void login(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    ) {
        throw new IllegalStateException("Add Spring Security to handle authentication");
    }

    /**
     * Implemented by Spring Security
     */
    @ApiOperation(value = "Logout", notes = "Logout the current user.")
    @ApiResponses({@ApiResponse(code = 200, message = "")})
    @PostMapping(value = "/auth/logout")
    void logout() {
        throw new IllegalStateException("Add Spring Security to handle authentication");
    }
}
