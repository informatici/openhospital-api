package org.isf.permissions.dto;

import java.util.List;

public class PermissionDTO extends LitePermissionDTO {

	private Integer id;
	private String description;
	private List<String> userGroupIds;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getUserGroupIds() {
		return userGroupIds;
	}

	public void setUserGroupIds(List<String> userGroupIds) {
		this.userGroupIds = userGroupIds;
	}



}
