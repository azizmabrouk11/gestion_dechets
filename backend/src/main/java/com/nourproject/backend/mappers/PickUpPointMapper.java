package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointUpdateDto;
import com.nourproject.backend.entities.PickUpPoint;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PickUpPointMapper {

    @Mapping(target = "_id", ignore = true)
    PickUpPoint pickUpPointDtoToPickUpPoint(PickUpPointDto pickUpPointDto);

    @Mapping(source = "_id", target = "id")
    PickUpPointDto pickUpPointToPickUpPointDto(PickUpPoint pickUpPoint);

    @Mapping(target = "_id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePickUpPointFromDto(PickUpPointUpdateDto pickUpPointUpdateDto, @MappingTarget PickUpPoint pickUpPoint);
}
