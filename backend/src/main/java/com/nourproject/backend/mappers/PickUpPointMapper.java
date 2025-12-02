package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointUpdateDto;
import com.nourproject.backend.entities.PickUpPoint;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PickUpPointMapper {

    PickUpPoint pickUpPointDtoToPickUpPoint(PickUpPointDto pickUpPointDto);

    PickUpPointDto pickUpPointToPickUpPointDto(PickUpPoint pickUpPoint);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePickUpPointFromDto(PickUpPointUpdateDto pickUpPointUpdateDto, @MappingTarget PickUpPoint pickUpPoint);
}
