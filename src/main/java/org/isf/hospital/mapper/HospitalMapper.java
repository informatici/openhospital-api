package org.isf.hospital.mapper;

import org.isf.hospital.dto.HospitalDTO;
import org.isf.hospital.model.Hospital;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class HospitalMapper extends GenericMapper<Hospital, HospitalDTO> {

    public HospitalMapper() {
        super(Hospital.class, HospitalDTO.class);
    }
}