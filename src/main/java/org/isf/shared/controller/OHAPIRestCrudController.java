package org.isf.shared.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.manager.CRUDManager;
import org.isf.shared.responsebodyadvice.DTO;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


public abstract class OHAPIRestCrudController <T, DTOT, ID extends Serializable, M extends CRUDManager<T, ID>> {

	@Autowired
	protected M manager;
	private Logger logger = LoggerFactory.getLogger(OHAPIRestCrudController.class);
	private static final ModelMapper modelMapper = new ModelMapper();
	private static final String DEFAULT_PAGE_SIZE = "20";
	//public DTOT dtoType = new DTOT();

	protected Object map(Object value) {
		Class<DTOT> dto = (Class<DTOT>) GenericTypeResolver.resolveTypeArguments(getClass(), OHAPIRestCrudController.class)[1];
		Object returnValue;
		if (value instanceof Page) {
			returnValue = ((Page<T>) value).map(it -> modelMapper.map(it, dto));
		} else if (value instanceof Collection) {
			returnValue = ((Collection<T>) value).stream().map(it -> modelMapper.map(it, dto)).collect(Collectors.toList());
		} else {
			returnValue = modelMapper.map(value, dto);
		}
		return returnValue;
	}


	@PostMapping(produces = "application/vnd.ohapi.app-v1+json")
	public DTOT create(@RequestBody T instance) throws OHServiceException {
		logger.debug(instance.toString());
		manager.create(instance);
		return (DTOT) map(instance);
	}

	@PutMapping(value = "/{id}", produces = "application/vnd.ohapi.app-v1+json")
	public DTOT update(@PathVariable ID iId, @RequestBody T instance) throws OHServiceException {
		logger.debug(instance.toString());
		manager.update(instance);
		return (DTOT) map(instance);
	}
	

	@DeleteMapping(value = "/{code}", produces = "application/vnd.ohapi.app-v1+json")
	public void delete(@PathVariable ID code) throws OHServiceException {
		T p = manager.get(code);
		if(p != null) {
			manager.delete(p);
		}
	}

	@GetMapping(produces = "application/vnd.ohapi.app-v1+json")
	public List<DTOT> getPage(
			@RequestParam(value="page", required=false, defaultValue="0") Integer page,
			@RequestParam(value="size", required=false, defaultValue=DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
		return (List<DTOT>) map(manager.getPage(page, size));
	}
	
	@GetMapping(value = "/{code}", produces = "application/vnd.ohapi.app-v1+json")
	public DTOT get(@PathVariable ID code) throws OHServiceException {
		T instance = manager.get(code);
		if (instance == null) {
			throw new OHAPIException(
				new OHExceptionMessage("Not Found", "Code " + code + " not found", OHSeverityLevel.ERROR),
				HttpStatus.NOT_FOUND
			);
		}
		return (DTOT) map(instance);
	}
}
