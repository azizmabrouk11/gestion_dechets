package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {
    
    // Find routes by date
    List<Route> findByRouteDate(LocalDateTime routeDate);
    
    // Find routes between dates
    List<Route> findByRouteDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find routes by vehicle - using _id field
    List<Route> findByVehicule__id(String vehiculeId);
    
    // Find the most recent route
    Optional<Route> findFirstByOrderByRouteDateDesc();
    
    // Find routes for today
    List<Route> findByRouteDateGreaterThanEqualAndRouteDateLessThan(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
