package org.isf.shared.rest;

import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public abstract class OHApiAbstractController<S, T> {

    @Autowired
    protected ModelMapper modelMapper;

    protected T toDTO(S model) {
        return modelMapper.map(model, getDTOClass());
    }

    protected S toModel(T dto) {
        return modelMapper.map(dto, getModelClass());
    }

    protected List<T> toDTOList(List list) {
        return (List<T>) list.stream().map(it -> modelMapper.map(it, getDTOClass())).collect(Collectors.toList());
    }

    abstract protected Class<T> getDTOClass();

    abstract protected Class<S> getModelClass();


}
