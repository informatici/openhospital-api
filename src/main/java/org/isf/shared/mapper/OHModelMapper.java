package org.isf.shared.mapper;

import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author akashytsa
 */
@Component
public class OHModelMapper {


    private ModelMapper modelMapper;

    public OHModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(new BlobToByteArrayConverter());
        modelMapper.addConverter(new ByteArrayToBlobConverter());
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

}