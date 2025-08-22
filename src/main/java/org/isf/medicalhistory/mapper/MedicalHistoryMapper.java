package org.isf.medicalhistory.mapper;

import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class MedicalHistoryMapper extends GenericMapper<MedicalHistory, MedicalHistoryDTO> {
	public MedicalHistoryMapper() {
		super(MedicalHistory.class, MedicalHistoryDTO.class);
	}
}
