package org.isf.permissions.mapper;

import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.model.Permission;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper extends GenericMapper<Permission, PermissionDTO> {
		
	public PermissionMapper() {
			super(Permission.class, PermissionDTO.class);
		}
	
	}
