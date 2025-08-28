package org.isf.conditioning.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.isf.admission.rest.AdmissionController;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.conditioning.service.ConditioningOperations;
import org.isf.menu.model.User;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Conditioning")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ConditioningController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditioningController.class);

	private final ConditioningBrowserManager browserManager;
	private final ConditioningMapper conditioningMapper;

	public ConditioningController(ConditioningBrowserManager browserManager, ConditioningMapper conditioningMapper) {
		this.browserManager = browserManager;
		this.conditioningMapper = conditioningMapper;
	}

	@PutMapping("/conditionings")
	public ResponseEntity<ConditioningDTO> updateConditioning(@RequestBody @Valid ConditioningDTO updateConditioningDTO)
		throws OHServiceException {

		Conditioning old = browserManager.getConditioning(updateConditioningDTO.getId());
		if (old == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not found."));
		}

		Conditioning updateConditioning = conditioningMapper.map2Model(updateConditioningDTO);

		if (updateConditioningDTO.getPerformById() != null) {
			User user = browserManager.getConditioning(updateConditioningDTO.getId()).getPerformBy();
			if (user == null) {
				throw new OHAPIException(new OHExceptionMessage("User (performBy) not found."));
			}
			updateConditioning.setPerformBy(user);
		} else {
			throw new OHAPIException(new OHExceptionMessage("performBy field is required."));
		}

		browserManager.validateConditioning(updateConditioning);

		Conditioning updatedConditioning = browserManager.updateConditioning(updateConditioning);
		if (updatedConditioning == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not updated."));
		}

		return ResponseEntity.ok(conditioningMapper.map2DTO(updatedConditioning));
	}
}
