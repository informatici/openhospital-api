package org.isf.usergroups.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GroupPermissionsDTO(
	@Schema(name = "permissions", example = "[48, 24]", description = "List of permissions' ids") List<Integer> permissions) {

}
