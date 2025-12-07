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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDto {
    private String id;
    private List<PickUpPoint> pickUpPoints;
    private List<String> pickUpPointIds;
    private Vehicule vehicule;
    private String vehiculeId;
    private List<User> users;
    private List<String> userIds;
    private LocalDateTime routeDate;

    private Double totalDistance;
    private Long totalTime;
    private String encodedPolyline;
    private List<RouteInstructionDTO> instructions;
    private RouteStatus status;
}
