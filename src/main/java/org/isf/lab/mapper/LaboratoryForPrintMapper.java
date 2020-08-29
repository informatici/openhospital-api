package org.isf.lab.mapper;

import org.isf.lab.dto.LaboratoryForPrintDTO;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryForPrintMapper extends GenericMapper<LaboratoryForPrint, LaboratoryForPrintDTO> {

    public LaboratoryForPrintMapper() {
        super(LaboratoryForPrint.class, LaboratoryForPrintDTO.class);
    }
}