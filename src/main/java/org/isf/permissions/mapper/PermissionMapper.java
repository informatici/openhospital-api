package org.isf.permissions.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.model.Permission;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper extends GenericMapper<Permission, PermissionDTO> {

	public PermissionMapper() {
		super(Permission.class, PermissionDTO.class);
	}

	@Override
	public List<PermissionDTO> map2DTOList(List<Permission> list) {
		return list.stream().map(permission -> {
			PermissionDTO dto = modelMapper.map(permission, PermissionDTO.class);
			List<String> userGroupCodes = extractUserGroupCodes(permission);
			dto.setUserGroupIds(userGroupCodes);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public PermissionDTO map2DTO(Permission fromObj) {
		PermissionDTO dto = modelMapper.map(fromObj, PermissionDTO.class);
		List<String> userGroupCodes = extractUserGroupCodes(fromObj);
		dto.setUserGroupIds(userGroupCodes);
		return dto;
	}

	private List<String> extractUserGroupCodes(Permission fromObj) {
		if (fromObj.getGroupPermission() == null) {
			return new ArrayList<>();
		}
		return fromObj.getGroupPermission().stream().map(gp -> gp.getUserGroup().getCode()).collect(Collectors.toList());
	}

}
