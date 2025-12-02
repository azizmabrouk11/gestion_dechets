package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteUpdateDto;
import com.nourproject.backend.services.interfaces.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<Response> getAllRoutes() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getRouteById(@PathVariable String id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @GetMapping("/date/{routeDate}")
    public ResponseEntity<Response> getRoutesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime routeDate) {
        return ResponseEntity.ok(routeService.findByRouteDate(routeDate));
    }

    @GetMapping("/date-range")
    public ResponseEntity<Response> getRoutesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(routeService.findByDateRange(startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<Response> createRoute(@Valid @RequestBody RouteDto routeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.save(routeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateRoute(
            @PathVariable String id,
            @Valid @RequestBody RouteUpdateDto routeUpdateDto) {
        return ResponseEntity.ok(routeService.updateById(id, routeUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteRoute(@PathVariable String id) {
        return ResponseEntity.ok(routeService.deleteById(id));
    }
}
