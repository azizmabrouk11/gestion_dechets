package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.container.ContainerDto;
import com.nourproject.backend.dtos.container.ContainerUpdateDto;
import com.nourproject.backend.entities.Container;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    @Mapping(target = "_id", ignore = true)
    Container containerDtoToContainer(ContainerDto containerDto);

    @Mapping(source = "_id", target = "id")
    ContainerDto containerToContainerDto(Container container);

    @Mapping(target = "_id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateContainerFromDto(ContainerUpdateDto containerUpdateDto, @MappingTarget Container container);
}
