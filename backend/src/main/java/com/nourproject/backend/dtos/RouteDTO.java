package com.nourproject.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private String id;
    private List<String> pickUpPointIds;
    private String vehiculeId;
    private List<String> userIds;
    private LocalDateTime routeDate;
    private Double totalDistance; // in meters
    private Long totalTime; // in milliseconds
    private String encodedPolyline; // GraphHopper encoded route
    private List<RouteInstructionDTO> instructions;
}
