package org.isf.admtype.mapper;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.model.AdmissionType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class AdmissionTypeMapper extends GenericMapper<AdmissionType, AdmissionTypeDTO> {
	public AdmissionTypeMapper() {
		super(AdmissionType.class, AdmissionTypeDTO.class);
	}
}