package org.isf.menu.mapper;

import org.isf.menu.dto.UserGroupDTO;
import org.isf.menu.model.UserGroup;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class UserGroupMapper extends GenericMapper<UserGroup, UserGroupDTO> {
	public UserGroupMapper() {
		super(UserGroup.class, UserGroupDTO.class);
	}
}
