package com.nourproject.backend.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nourproject.backend.dtos.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Response {
    //generic
    private int status;
    private String message;
   


    //user data output
    private UserDto user;
    private List<UserDto> userList;


    //Payment data output
    // private NotificationDto notification;
    // private List<NotificationDto> notifications;

    private final LocalDateTime time = LocalDateTime.now();


}
