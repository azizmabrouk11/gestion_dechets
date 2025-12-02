package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.Route;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RouteRepository extends MongoRepository<Route, String> {
    List<Route> findByRouteDate(LocalDateTime routeDate);
    List<Route> findByRouteDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
