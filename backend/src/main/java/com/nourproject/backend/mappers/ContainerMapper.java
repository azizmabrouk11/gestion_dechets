package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.container.ContainerDto;
import com.nourproject.backend.dtos.container.ContainerUpdateDto;
import com.nourproject.backend.entities.Container;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    Container containerDtoToContainer(ContainerDto containerDto);

    ContainerDto containerToContainerDto(Container container);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateContainerFromDto(ContainerUpdateDto containerUpdateDto, @MappingTarget Container container);
}
