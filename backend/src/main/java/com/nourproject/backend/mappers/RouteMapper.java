package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteUpdateDto;
import com.nourproject.backend.entities.Route;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(target = "_id", ignore = true)
    Route routeDtoToRoute(RouteDto routeDto);

    @Mapping(source = "_id", target = "id")
    RouteDto routeToRouteDto(Route route);

    @Mapping(target = "_id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRouteFromDto(RouteUpdateDto routeUpdateDto, @MappingTarget Route route);
}
