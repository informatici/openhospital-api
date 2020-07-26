package org.isf.menu.mapper;

import org.isf.menu.dto.UserDTO;
import org.isf.menu.model.User;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends GenericMapper<User, UserDTO> {
	public UserMapper() {
		super(User.class, UserDTO.class);
	}
}
