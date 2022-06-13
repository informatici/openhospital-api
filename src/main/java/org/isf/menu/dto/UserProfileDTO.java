package org.isf.menu.dto;

import java.util.List;

public class UserProfileDTO {

	private String userName;

	private List<String> permission;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getPermission() {
		return permission;
	}

	public void setPermission(List<String> permission) {
		this.permission = permission;
	}
	
}
