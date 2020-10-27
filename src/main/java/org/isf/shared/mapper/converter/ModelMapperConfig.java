package org.isf.shared.mapper.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Autowired
    protected BlobToByteArrayConverter blobToByteArrayConverter;

    @Autowired
    protected ByteArrayToBlobConverter byteArrayToBlobConverter;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(blobToByteArrayConverter);
        modelMapper.addConverter(byteArrayToBlobConverter);
        return modelMapper;
    }
}