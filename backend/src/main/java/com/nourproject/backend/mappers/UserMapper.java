package com.nourproject.backend.mappers;


import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.user.UserUpdateDto;
import com.nourproject.backend.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User userDtoToUser(UserDto userDto);
    
    UserDto userToUserDto(User user);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "faceAuthEnabled", ignore = true)
    void updateUserUpdateDtoToUser(UserUpdateDto userUpdateDto, @MappingTarget User user);
    
    /**
     * Convert UserDto to UserUpdateDto for reusing update logic
     * This eliminates code duplication in sync operations
     */
    UserUpdateDto userDtoToUserUpdateDto(UserDto userDto);
}

