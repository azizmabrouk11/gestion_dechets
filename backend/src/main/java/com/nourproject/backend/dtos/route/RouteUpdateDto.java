package com.nourproject.backend.dtos.route;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.backend.entities.PickUpPoint;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.entities.Vehicule;
import com.nourproject.backend.enums.RouteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
@Builder
public class RouteUpdateDto {
    private List<PickUpPoint> pickUpPoints;
    private Vehicule vehicule;
    private List<User> users;
    private LocalDateTime routeDate;
    private RouteStatus status;
}
