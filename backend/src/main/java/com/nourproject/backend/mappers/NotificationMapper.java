package com.nourproject.backend.mappers;


import com.nourproject.backend.dtos.Notification.NotificationDto;
import com.nourproject.backend.dtos.Notification.NotificationUpdateDto;
import com.nourproject.backend.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "_id", target = "id")
    NotificationDto toDto(Notification notification);
    
    @Mapping(target = "_id", ignore = true)
    Notification toEntity(NotificationDto notificationDto);
    
    @Mapping(target = "_id", ignore = true)
    void updateNotificationFromDto(NotificationUpdateDto notificationDto, @MappingTarget Notification notification);
}
