package org.isf.menu.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class GroupMenuDTO {
	@ApiModelProperty(notes="code of the group menu", example = "1", position = 1)
	private Integer code;
	
	@NotNull
	@ApiModelProperty(notes="the related user group's code", example = "labo", position = 2)
	private String userGroup;
	
	@NotNull
	@ApiModelProperty(notes="the related menu item's code", example = "admtype", position = 3)
	private String menuItem;

	public GroupMenuDTO() {
		super();
	}

	public GroupMenuDTO(Integer code, String userGroup, String menuItem) {
		super();
		this.code = code;
		this.userGroup = userGroup;
		this.menuItem = menuItem;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(String menuItem) {
		this.menuItem = menuItem;
	}
	
}
