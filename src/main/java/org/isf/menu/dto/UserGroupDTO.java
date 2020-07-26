package org.isf.menu.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class UserGroupDTO {
	@NotNull
	@ApiModelProperty(notes="name of the group (must be unique)", example = "labo", position = 1)
	private String code;
	
	@ApiModelProperty(notes="the description of the group", example = "Staff members working in the laboratory", position = 2)
	private String desc;
	
	public UserGroupDTO() {
		super();
	}

	public UserGroupDTO(String code, String desc) {
		super();
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
