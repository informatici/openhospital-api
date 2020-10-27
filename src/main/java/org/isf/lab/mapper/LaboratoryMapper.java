package org.isf.lab.mapper;

import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.model.Laboratory;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryMapper extends GenericMapper<Laboratory, LaboratoryDTO> {

    public LaboratoryMapper() {
        super(Laboratory.class, LaboratoryDTO.class);
    }
}