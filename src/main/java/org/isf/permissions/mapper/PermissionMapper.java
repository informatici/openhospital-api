package org.isf.permissions.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.permissions.dto.GroupsPermissionDTO;
import org.isf.permissions.model.Permission;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper extends GenericMapper<Permission, GroupsPermissionDTO> {

	public PermissionMapper() {
		super(Permission.class, GroupsPermissionDTO.class);
	}

	@Override
	public List<GroupsPermissionDTO> map2DTOList(List<Permission> list) {
		return list.stream().map(permission -> {
			GroupsPermissionDTO dto = modelMapper.map(permission, GroupsPermissionDTO.class);
			List<String> userGroupCodes = extractUserGroupCodes(permission);
			dto.setUserGroupIds(userGroupCodes);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public GroupsPermissionDTO map2DTO(Permission fromObj) {
		GroupsPermissionDTO dto = modelMapper.map(fromObj, GroupsPermissionDTO.class);
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
