package com.nourproject.backend.mappers;

import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.user.UserUpdateDto;
import com.nourproject.backend.dtos.vehicule.VehiculeDto;
import com.nourproject.backend.dtos.vehicule.VehiculeUpdateDto;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.entities.Vehicule;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(target = "_id", ignore = true)
    Vehicule vehiculeDtoToVehicule(VehiculeDto vehiculeDto);

    @Mapping(source = "_id", target = "id")
    VehiculeDto vehiculeToVehiculeDto(Vehicule vehicule);

    @Mapping(target = "_id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVehiculeFromDto(VehiculeUpdateDto vehiculeUpdateDto, @MappingTarget Vehicule vehicule);


}
