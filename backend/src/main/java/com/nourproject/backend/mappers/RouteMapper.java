package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteUpdateDto;
import com.nourproject.backend.entities.Route;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    Route routeDtoToRoute(RouteDto routeDto);

    RouteDto routeToRouteDto(Route route);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRouteFromDto(RouteUpdateDto routeUpdateDto, @MappingTarget Route route);
}
