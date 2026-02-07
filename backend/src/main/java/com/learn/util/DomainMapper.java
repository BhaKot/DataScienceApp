package com.learn.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper {

    private final ModelMapper modelMapper;

    public DomainMapper() {
        this.modelMapper = new ModelMapper();
    }

    public <S, D> D toDTO(S source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationClass);
    }

    public <S, D> D toEntity(S source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationClass);
    }
}
