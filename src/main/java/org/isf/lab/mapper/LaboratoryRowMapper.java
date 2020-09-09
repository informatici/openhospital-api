package org.isf.lab.mapper;

import org.isf.lab.dto.LaboratoryRowDTO;
import org.isf.lab.model.LaboratoryRow;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryRowMapper extends GenericMapper<LaboratoryRow, LaboratoryRowDTO> {

    public LaboratoryRowMapper() {
        super(LaboratoryRow.class, LaboratoryRowDTO.class);
    }
}