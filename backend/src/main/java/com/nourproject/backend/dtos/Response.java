package com.nourproject.backend.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nourproject.backend.dtos.container.ContainerDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.vehicule.VehiculeDto;
import com.nourproject.backend.entities.Incident;
import com.nourproject.backend.entities.Route;
import com.nourproject.backend.entities.Vehicule;
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

    private PickUpPointDto pickUpPoint;
    private List<PickUpPointDto> pickUpPoints;

    private ContainerDto container;
    private List<ContainerDto> containers;

    private RouteDto route;
    private List<RouteDto> routes;
  
//
//    private Incident incident;
//    private List<Incident> incidents;
//
//    private Route route;
//    private List<Route> routes;
//





    //Payment data output
    // private NotificationDto notification;
    // private List<NotificationDto> notifications;

    private final LocalDateTime time = LocalDateTime.now();


}
