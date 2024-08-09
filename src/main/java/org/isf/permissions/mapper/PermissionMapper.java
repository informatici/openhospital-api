/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.permissions.mapper;

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
		return list.stream().map(permission -> modelMapper.map(permission, PermissionDTO.class)).collect(Collectors.toList());
	}

	@Override
	public PermissionDTO map2DTO(Permission fromObj) {
		return modelMapper.map(fromObj, PermissionDTO.class);
	}
}
