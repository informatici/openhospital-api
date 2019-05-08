package org.isf.admtype.rest;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeManagerCrudAdapter;
import org.isf.admtype.model.AdmissionType;
import org.isf.shared.controller.OHAPIRestCrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/admissiontypes")
@Api(value="/admissiontypes",produces ="application/vnd.ohapi.app-v1+json")
public class AdmissionTypeController extends OHAPIRestCrudController<AdmissionType, 
	AdmissionType, String, AdmissionTypeManagerCrudAdapter>{

}
