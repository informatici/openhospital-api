package org.isf.menu.dto;

import java.util.List;

public class UserProfileDTO {

	private String userName;
	private String userGroupName;
	private String userDesc;

	private List<String> permissions;

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permission) {
		this.permissions = permission;
	}
	
}
