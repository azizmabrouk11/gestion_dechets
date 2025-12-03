package com.nourproject.backend.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;

import com.nourproject.backend.dtos.container.ContainerDto;
import com.nourproject.backend.dtos.incident.IncidentDto;
import com.nourproject.backend.dtos.Notification.NotificationDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.vehicule.VehiculeDto;
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
    private List<UserDto> users;

    private VehiculeDto vehicule;
    private List<VehiculeDto> vehicules;

    private PickUpPointDto pickuppoint;
    private List<PickUpPointDto> pickuppoints;

    private ContainerDto container;
    private List<ContainerDto> containers;

    private RouteDto route;
    private List<RouteDto> routes;

    private IncidentDto incident;
    private List<IncidentDto> incidents;

    private NotificationDto notification;
    private List<NotificationDto> notifications;

    private final LocalDateTime time = LocalDateTime.now();


}
