package org.isf.shared.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

public abstract class OHApiAbstractController<S, T> {

    protected ModelMapper modelMapper;

    protected OHApiAbstractController(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

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
