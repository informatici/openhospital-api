package org.isf.shared.mapper;

import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.modelmapper.ModelMapper;

/**
 * @author akashytsa
 */
public class OHModelMapper {

    private static ModelMapper modelMapper;

    private static ModelMapper getInstance() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(new BlobToByteArrayConverter());
        modelMapper.addConverter(new ByteArrayToBlobConverter());
        return modelMapper;
    }

    public static ModelMapper getObjectMapper() {
        return modelMapper == null ? getInstance() : modelMapper;
    }
}