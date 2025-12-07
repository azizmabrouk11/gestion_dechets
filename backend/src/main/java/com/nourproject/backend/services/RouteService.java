package com.nourproject.backend.services;

import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteInstructionDTO;
import com.nourproject.backend.entities.PickUpPoint;
import com.nourproject.backend.entities.Route;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.entities.Vehicule;
import com.nourproject.backend.enums.RouteStatus;
import com.nourproject.backend.repositories.PickUpPointRepository;
import com.nourproject.backend.repositories.RouteRepository;
import com.nourproject.backend.repositories.UserRepository;
import com.nourproject.backend.repositories.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {
    
    private final RouteRepository routeRepository;
    private final PickUpPointRepository pickUpPointRepository;
    private final VehiculeRepository vehiculeRepository;
    private final UserRepository userRepository;

    /**
     * Create a new route from GraphHopper optimization result
     */
    public Route createRoute(RouteDto routeDTO) {
        // Fetch pickup points
        List<PickUpPoint> pickUpPoints = routeDTO.getPickUpPointIds().stream()
                .map(id -> pickUpPointRepository.findById(id).orElse(null))
                .filter(point -> point != null)
                .collect(Collectors.toList());

        // Fetch vehicle
        Vehicule vehicule = vehiculeRepository.findById(routeDTO.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Fetch users (drivers/collectors)
        List<User> users = routeDTO.getUserIds().stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());

        // Create route entity
        Route route = Route.builder()
                .pickUpPoints(pickUpPoints)
                .vehicule(vehicule)
                .users(users)
                .routeDate(routeDTO.getRouteDate() != null ? routeDTO.getRouteDate() : LocalDateTime.now())
                .totalDistance(routeDTO.getTotalDistance())
                .totalTime(routeDTO.getTotalTime())
                .encodedPolyline(routeDTO.getEncodedPolyline())
                .instructions(routeDTO.getInstructions() != null ? routeDTO.getInstructions() : new ArrayList<>())
                .status(RouteStatus.planned)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return routeRepository.save(route);
    }

    /**
     * Get all routes
     */
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    /**
     * Get route by ID
     */
    public Route getRouteById(String id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    /**
     * Get routes for a specific date
     */
    public List<Route> getRoutesByDate(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return routeRepository.findByRouteDateGreaterThanEqualAndRouteDateLessThan(startOfDay, endOfDay);
    }

    /**
     * Get routes for today
     */
    public List<Route> getTodayRoutes() {
        return getRoutesByDate(LocalDateTime.now());
    }

    /**
     * Get routes by vehicle
     */
    public List<Route> getRoutesByVehicle(String vehicleId) {
        return routeRepository.findByVehicule__id(vehicleId);
    }

    /**
     * Update route status
     */
    public Route updateRouteStatus(String routeId, RouteStatus status) {
        Route route = getRouteById(routeId);
        route.setStatus(status);
        route.setUpdatedAt(LocalDateTime.now());
        return routeRepository.save(route);
    }

    /**
     * Delete route
     */
    public void deleteRoute(String id) {
        routeRepository.deleteById(id);
    }

    /**
     * Get the most recent route
     */
    public Route getLatestRoute() {
        return routeRepository.findFirstByOrderByRouteDateDesc()
                .orElse(null);
    }
}
