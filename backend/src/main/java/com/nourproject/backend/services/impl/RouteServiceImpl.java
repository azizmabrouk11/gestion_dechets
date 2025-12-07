package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteUpdateDto;
import com.nourproject.backend.entities.Route;
import com.nourproject.backend.exceptions.NotFoundException;
import com.nourproject.backend.mappers.RouteMapper;
import com.nourproject.backend.repositories.RouteRepository;
import com.nourproject.backend.services.interfaces.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;
    private final com.nourproject.backend.repositories.PickUpPointRepository pickUpPointRepository;
    private final com.nourproject.backend.repositories.VehiculeRepository vehiculeRepository;
    private final com.nourproject.backend.repositories.UserRepository userRepository;

    @Override
    public Response findAll() {
        List<Route> routes = routeRepository.findAll();
        
        // Manually populate lazy-loaded pickup points for each route
        routes.forEach(route -> {
            if (route.getPickUpPoints() != null) {
                // Trigger lazy loading by accessing the collection
                route.getPickUpPoints().size();
            }
            if (route.getVehicule() != null) {
                // Trigger lazy loading for vehicle
                route.getVehicule().get_id();
            }
            if (route.getUsers() != null) {
                // Trigger lazy loading for users
                route.getUsers().size();
            }
        });
        
        List<RouteDto> list = routes.stream()
                .map(routeMapper::routeToRouteDto).toList();

        return Response.builder()
                .status(200)
                .message("List of routes retrieved successfully")
                .routes(list)
                .build();
    }

    @Override
    public Response findById(String id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route with ID " + id + " not found"));
        
        // Manually populate lazy-loaded references
        if (route.getPickUpPoints() != null) {
            route.getPickUpPoints().size();
        }
        if (route.getVehicule() != null) {
            route.getVehicule().get_id();
        }
        if (route.getUsers() != null) {
            route.getUsers().size();
        }
        
        RouteDto routeDto = routeMapper.routeToRouteDto(route);
        
        return Response.builder()
                .status(200)
                .message("Route retrieved successfully")
                .route(routeDto)
                .build();
    }

    @Override
    public Response findByRouteDate(LocalDateTime routeDate) {
        List<RouteDto> list = routeRepository.findByRouteDate(routeDate).stream()
                .map(routeMapper::routeToRouteDto).toList();
        
        return Response.builder()
                .status(200)
                .message("Routes for date retrieved successfully")
                .routes(list)
                .build();
    }

    @Override
    public Response findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<RouteDto> list = routeRepository.findByRouteDateBetween(startDate, endDate).stream()
                .map(routeMapper::routeToRouteDto).toList();
        
        return Response.builder()
                .status(200)
                .message("Routes in date range retrieved successfully")
                .routes(list)
                .build();
    }

    @Override
    public Response checkDuplicate(List<String> pickUpPointIds) {
        System.out.println("Checking duplicate for IDs: " + pickUpPointIds);
        if (pickUpPointIds == null || pickUpPointIds.isEmpty()) {
            return Response.builder()
                    .status(400)
                    .message("No pickup points provided")
                    .build();
        }

        List<String> sortedIds = new java.util.ArrayList<>(pickUpPointIds);
        java.util.Collections.sort(sortedIds);
        
        List<Route> existingRoutes = routeRepository.findAll();
        for (Route existingRoute : existingRoutes) {
            if (existingRoute.getPickUpPoints() != null && !existingRoute.getPickUpPoints().isEmpty()) {
                List<String> existingIds = existingRoute.getPickUpPoints().stream()
                    .map(com.nourproject.backend.entities.PickUpPoint::get_id)
                    .sorted()
                    .collect(java.util.stream.Collectors.toList());
                
                if (sortedIds.equals(existingIds)) {
                    System.out.println("Duplicate found in checkDuplicate: " + existingRoute.get_id());
                    return Response.builder()
                            .status(409)
                            .message("Duplicate route found - this combination of pickup points already exists")
                            .build();
                }
            }
        }
        
        System.out.println("No duplicate found in checkDuplicate");
        return Response.builder()
                .status(200)
                .message("No duplicate route found - safe to create")
                .build();
    }

    @Override
    public Response save(RouteDto routeDto) {
        // Debug: Check received data
        System.out.println("Received RouteDto with pickUpPointIds: " + 
            (routeDto.getPickUpPointIds() != null ? routeDto.getPickUpPointIds().size() : "null"));
        
        // Check for duplicate route with same pickup points
        if (routeDto.getPickUpPointIds() != null && !routeDto.getPickUpPointIds().isEmpty()) {
            System.out.println("Checking for duplicate routes...");
            List<String> sortedIds = new java.util.ArrayList<>(routeDto.getPickUpPointIds());
            java.util.Collections.sort(sortedIds);
            
            // Find all routes and check if any has the same pickup points
            List<Route> existingRoutes = routeRepository.findAll();
            for (Route existingRoute : existingRoutes) {
                if (existingRoute.getPickUpPoints() != null && !existingRoute.getPickUpPoints().isEmpty()) {
                    List<String> existingIds = existingRoute.getPickUpPoints().stream()
                        .map(com.nourproject.backend.entities.PickUpPoint::get_id)
                        .sorted()
                        .collect(java.util.stream.Collectors.toList());
                    
                    if (sortedIds.equals(existingIds)) {
                        System.out.println("Duplicate route found! Route ID: " + existingRoute.get_id());
                        return Response.builder()
                                .status(409)
                                .message("A route with the same pickup points already exists (ID: " + existingRoute.get_id() + ")")
                                .build();
                    }
                }
            }
            System.out.println("No duplicate found, proceeding with route creation...");
        }
        
        Route route = routeMapper.routeDtoToRoute(routeDto);
        
        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        route.setCreatedAt(now);
        route.setUpdatedAt(now);
        
        // Manual mapping for IDs - ensure pickup points are loaded
        if (routeDto.getPickUpPointIds() != null && !routeDto.getPickUpPointIds().isEmpty()) {
            System.out.println("Looking up " + routeDto.getPickUpPointIds().size() + " pickup points...");
            System.out.println("Pickup point IDs from request: " + routeDto.getPickUpPointIds());
            
            // Try finding one by one for debugging
            for (String id : routeDto.getPickUpPointIds()) {
                System.out.println("Trying to find ID: " + id);
                var found = pickUpPointRepository.findById(id);
                System.out.println("  Result: " + (found.isPresent() ? "FOUND" : "NOT FOUND"));
                if (found.isPresent()) {
                    System.out.println("  Actual ID in entity: " + found.get().get_id());
                }
            }
            
            // Query database using standard findAllById which handles conversion based on @MongoId
            List<com.nourproject.backend.entities.PickUpPoint> points = pickUpPointRepository.findAllById(routeDto.getPickUpPointIds());
            System.out.println("Found " + points.size() + " pickup points in database");
            
            if (points.isEmpty()) {
                System.out.println("WARNING: No pickup points found in database for provided IDs!");
            } else {
                System.out.println("Found pickup points IDs: " + points.stream()
                    .map(com.nourproject.backend.entities.PickUpPoint::get_id)
                    .collect(java.util.stream.Collectors.toList()));
                route.setPickUpPoints(points);
            }
        } else {
            System.out.println("WARNING: No pickUpPointIds received in request!");
        }
        
        if (routeDto.getVehiculeId() != null) {
            com.nourproject.backend.entities.Vehicule vehicule = vehiculeRepository.findById(routeDto.getVehiculeId())
                    .orElseThrow(() -> new NotFoundException("Vehicle with ID " + routeDto.getVehiculeId() + " not found"));
            route.setVehicule(vehicule);
        }
        
        if (routeDto.getUserIds() != null && !routeDto.getUserIds().isEmpty()) {
            List<com.nourproject.backend.entities.User> users = userRepository.findAllById(routeDto.getUserIds());
            route.setUsers(users);
        }
        
        // Set default status if not provided
        if (route.getStatus() == null) {
            route.setStatus(com.nourproject.backend.enums.RouteStatus.planned);
        }

        Route savedRoute = routeRepository.save(route);
        RouteDto savedDto = routeMapper.routeToRouteDto(savedRoute);
        
        // Debug: Log the pickUpPoints count
        System.out.println("Route saved with " + 
            (savedRoute.getPickUpPoints() != null ? savedRoute.getPickUpPoints().size() : 0) + 
            " pickup points");
        System.out.println("RouteDto has " + 
            (savedDto.getPickUpPoints() != null ? savedDto.getPickUpPoints().size() : 0) + 
            " pickup points");
        
        return Response.builder()
                .status(201)
                .message("Route created successfully")
                .route(savedDto)
                .build();
    }

    @Override
    public Response updateById(String id, RouteUpdateDto routeUpdateDto) {
        System.out.println("Update Route Request for ID: " + id);
        System.out.println("RouteUpdateDto: " + routeUpdateDto);
        System.out.println("VehiculeId: " + routeUpdateDto.getVehiculeId());
        System.out.println("UserIds: " + routeUpdateDto.getUserIds());
        System.out.println("Vehicule Object: " + routeUpdateDto.getVehicule());
        System.out.println("Users List: " + routeUpdateDto.getUsers());

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route with ID " + id + " not found"));
        
        // Update timestamp
        route.setUpdatedAt(LocalDateTime.now());
        
        // Handle vehicule update if provided
        if (routeUpdateDto.getVehiculeId() != null && !routeUpdateDto.getVehiculeId().trim().isEmpty()) {
            System.out.println("Updating vehicle with ID: " + routeUpdateDto.getVehiculeId());
            com.nourproject.backend.entities.Vehicule vehicule = vehiculeRepository.findById(routeUpdateDto.getVehiculeId())
                    .orElseThrow(() -> new NotFoundException("Vehicle with ID " + routeUpdateDto.getVehiculeId() + " not found"));
            route.setVehicule(vehicule);
        }
        
        // Handle users update if provided (allow multiple users)
        if (routeUpdateDto.getUserIds() != null && !routeUpdateDto.getUserIds().isEmpty()) {
            System.out.println("Updating users with IDs: " + routeUpdateDto.getUserIds());
            List<com.nourproject.backend.entities.User> users = new java.util.ArrayList<>();
            for (String userId : routeUpdateDto.getUserIds()) {
                // Skip null or empty user IDs
                if (userId != null && !userId.trim().isEmpty()) {
                    com.nourproject.backend.entities.User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
                    users.add(user);
                }
            }
            route.setUsers(users);
        } else {
            // If userIds is empty or null, clear the users list
            route.setUsers(new java.util.ArrayList<>());
        }
        
        // Update other fields using mapper (excluding vehicule and users which we already handled)
        if (routeUpdateDto.getStatus() != null) {
            route.setStatus(routeUpdateDto.getStatus());
        }
        if (routeUpdateDto.getRouteDate() != null) {
            route.setRouteDate(routeUpdateDto.getRouteDate());
        }
        
        Route updatedRoute = routeRepository.save(route);
        
        System.out.println("Route saved. Vehicle in route: " + updatedRoute.getVehicule());
        System.out.println("Users in route: " + updatedRoute.getUsers());
        
        // Manually populate lazy-loaded references
        if (updatedRoute.getPickUpPoints() != null) {
            updatedRoute.getPickUpPoints().size();
        }
        if (updatedRoute.getVehicule() != null) {
            System.out.println("Triggering lazy load for vehicle...");
            String vehicleId = updatedRoute.getVehicule().get_id();
            System.out.println("Vehicle ID after lazy load: " + vehicleId);
        }
        if (updatedRoute.getUsers() != null && !updatedRoute.getUsers().isEmpty()) {
            System.out.println("Triggering lazy load for users...");
            updatedRoute.getUsers().size();
            System.out.println("Users count: " + updatedRoute.getUsers().size());
        }
        
        RouteDto updatedDto = routeMapper.routeToRouteDto(updatedRoute);
        
        return Response.builder()
                .status(200)
                .message("Route updated successfully")
                .route(updatedDto)
                .build();
    }

    @Override
    @Transactional
    public Response deleteById(String id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route with ID " + id + " not found"));
        
        RouteDto deletedDto = routeMapper.routeToRouteDto(route);
        routeRepository.delete(route);
        
        return Response.builder()
                .status(200)
                .message("Route deleted successfully")
                .route(deletedDto)
                .build();
    }
}
