package org.isf.usergroups.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record GroupPermissionsDTO(
	@Schema(name = "permissions", example = "[48, 24]", description = "List of permissions' ids") List<Integer> permissions) {

}
