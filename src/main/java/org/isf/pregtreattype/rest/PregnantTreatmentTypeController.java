package org.isf.pregtreattype.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PregnantTreatmentTypeController extends OHApiAbstractController<PregnantTreatmentType, PregnantTreatmentTypeDTO> {
	
	@Autowired
	private PregnantTreatmentTypeBrowserManager manager;
	
	/**
	 * return the list of {@link PregnantTreatmentType}s
	 * 
	 * @return the list of {@link PregnantTreatmentType}s
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PregnantTreatmentTypeDTO> getPregnantTreatmentType() throws OHServiceException {
        return toDTOList(manager.getPregnantTreatmentType());
	}
	
	/**
	 * insert a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to insert
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean newPregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentType) throws OHServiceException {
        return manager.newPregnantTreatmentType(toModel(pregnantTreatmentType));
	}

	/**
	 * update a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@PatchMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean updatePregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentType) throws OHServiceException {
        return manager.updatePregnantTreatmentType(toModel(pregnantTreatmentType));
	}
	
	/**
	 * delete a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean deletePregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentType) throws OHServiceException {
        return manager.deletePregnantTreatmentType(toModel(pregnantTreatmentType));
	}
	
	/**
	 * check if the code is already in use
	 * 
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/pregtreattype/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean codeControl(@PathVariable String code) throws OHServiceException {
        return manager.codeControl(code);
	}

	@Override
	protected Class<PregnantTreatmentTypeDTO> getDTOClass() {
		return PregnantTreatmentTypeDTO.class;
	}

	@Override
	protected Class<PregnantTreatmentType> getModelClass() {
		return PregnantTreatmentType.class;
	}
}
