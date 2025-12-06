package com.nourproject.backend.entities;


import com.nourproject.backend.dtos.route.RouteInstructionDTO;
import com.nourproject.backend.enums.RouteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "routes")
public class Route {
    @Id
    private String _id;

    @DocumentReference(lazy = true)
    @Builder.Default
    private List<PickUpPoint> pickUpPoints = new ArrayList<>();

    @DocumentReference(lazy = true)
    private Vehicule vehicule;

    @DocumentReference(lazy = true)
    private List<User> users;

    private LocalDateTime routeDate;

    // GraphHopper route optimization data
    private Double totalDistance; // in meters
    private Long totalTime; // in milliseconds
    private String encodedPolyline; // GraphHopper encoded route for visualization

    @Builder.Default
    private List<RouteInstructionDTO> instructions = new ArrayList<>(); // Turn-by-turn directions

    private RouteStatus status; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
