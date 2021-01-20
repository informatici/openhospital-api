package org.isf.permissions.mapper;

import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.model.Permission;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class LitePermissionMapper extends GenericMapper<Permission, PermissionDTO> {
		
	public LitePermissionMapper() {
			super(Permission.class, PermissionDTO.class);
		}
	
	}
