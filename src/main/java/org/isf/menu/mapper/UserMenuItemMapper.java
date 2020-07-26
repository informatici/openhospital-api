package org.isf.menu.mapper;

import org.isf.menu.dto.UserMenuItemDTO;
import org.isf.menu.model.UserMenuItem;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMenuItemMapper extends GenericMapper<UserMenuItem, UserMenuItemDTO> {
	public UserMenuItemMapper() {
		super(UserMenuItem.class, UserMenuItemDTO.class);
	}
}
