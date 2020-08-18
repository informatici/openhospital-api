package org.isf.menu.mapper;

import org.isf.menu.dto.GroupMenuDTO;
import org.isf.menu.model.GroupMenu;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class GroupMenuMapper extends GenericMapper<GroupMenu, GroupMenuDTO> {
	public GroupMenuMapper() {
		super(GroupMenu.class, GroupMenuDTO.class);
	}
}
