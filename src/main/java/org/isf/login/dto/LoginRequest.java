package org.isf.login.dto;

import javax.validation.constraints.NotNull;


import io.swagger.annotations.ApiModelProperty;

public class LoginRequest {
  
	@NotNull
	@ApiModelProperty(notes = "user name", example = "admin", position = 1)
    private String username;

	@NotNull
	@ApiModelProperty(notes = "password of user", example = "admin", position = 2)
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
