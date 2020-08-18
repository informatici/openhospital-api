package org.isf.menu.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class UserDTO {
	@NotNull
	@ApiModelProperty(notes="the username (must be unique)", example = "John Doe", position = 1)	
	private String userName;

	@NotNull
	@ApiModelProperty(notes="the user's group", position = 2)
	private UserGroupDTO userGroupName;

	@NotNull
	@ApiModelProperty(notes="the user's password", example = "21@U2g423", position = 3)
	private String passwd;

	@ApiModelProperty(notes="the user's description", example = "Lab chief technician", position = 4)
	private String desc;
	
	public UserDTO() {
		super();
	}

	public UserDTO(String userName, UserGroupDTO userGroupName, String passwd, String desc) {
		super();
		this.userName = userName;
		this.userGroupName = userGroupName;
		this.passwd = passwd;
		this.desc = desc;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserGroupDTO getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(UserGroupDTO userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
