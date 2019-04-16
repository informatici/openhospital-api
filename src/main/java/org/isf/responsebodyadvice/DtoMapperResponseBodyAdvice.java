package org.isf.responsebodyadvice;

import java.util.Collection;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

@ControllerAdvice
public class DtoMapperResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

    //@Autowired
    //private ModelMapper modelMapper;
    private static final ModelMapper modelMapper = new ModelMapper();

    @Override
	protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
			MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
		// TODO Auto-generated method stub
        DTO ann = returnType.getMethodAnnotation(DTO.class);
        Assert.state(ann != null, "No Dto annotation");

        Class<?> dtoType = ann.value();
        Object value = bodyContainer.getValue();
        Object returnValue;

        if (value instanceof Page) {
            returnValue = ((Page<?>) value).map(it -> modelMapper.map(it, dtoType));
        } else if (value instanceof Collection) {
            returnValue = ((Collection<?>) value).stream().map(it -> modelMapper.map(it, dtoType));
        } else {
            returnValue = modelMapper.map(value, dtoType);
        }
        bodyContainer.setValue(returnValue);
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// TODO Auto-generated method stub
		return super.supports(returnType, converterType) && returnType.hasMethodAnnotation(DTO.class);
	}

}
