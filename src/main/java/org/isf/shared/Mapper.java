package org.isf.shared;

import java.util.List;

public interface Mapper <FromType, ToType> {
    public ToType map2DTO(FromType fromObj);
    public FromType map2Model(ToType toObj);
    public List<ToType> map2DTOList(List<FromType> list);
    public List<FromType> map2ModelList(List<ToType> list);
}