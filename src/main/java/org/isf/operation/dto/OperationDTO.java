package org.isf.operation.dto;

import javax.validation.constraints.NotNull;

import org.isf.opetype.dto.OperationTypeDTO;

public class OperationDTO {

	private String code;

	@NotNull
	private String description;

	@NotNull
	private OperationTypeDTO type;

	@NotNull
	private Integer major;

	private Integer lock;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OperationTypeDTO getType() {
		return type;
	}

	public void setType(OperationTypeDTO type) {
		this.type = type;
	}

	public Integer getMajor() {
		return major;
	}

	public void setMajor(Integer major) {
		this.major = major;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

}
