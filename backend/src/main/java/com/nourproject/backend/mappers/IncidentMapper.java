package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.incident.IncidentDto;
import com.nourproject.backend.dtos.incident.IncidentUpdateDto;
import com.nourproject.backend.entities.Incident;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IncidentMapper {
@Mapping(source = "_id", target = "id")
    IncidentDto toDto(Incident incident);

    @Mapping(target = "_id", ignore = true)
    Incident toEntity(IncidentDto incidentDto);

    @Mapping(target = "_id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIncidentFromDto(IncidentUpdateDto incidentUpdateDto, @MappingTarget Incident incident);
}
