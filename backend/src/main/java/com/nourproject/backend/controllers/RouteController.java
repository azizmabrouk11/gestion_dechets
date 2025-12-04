package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteUpdateDto;
import com.nourproject.backend.services.interfaces.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/public/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteService routeService;

    /**
     * Create a new route
     * POST /api/routes
     */
    @PostMapping
    public ResponseEntity<Response> createRoute(@RequestBody RouteDto routeDto) {
        Response response = routeService.save(routeDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Check for duplicate route
     * POST /api/public/routes/check-duplicate
     */
    @PostMapping("/check-duplicate")
    public ResponseEntity<Response> checkDuplicate(@RequestBody java.util.List<String> pickUpPointIds) {
        Response response = routeService.checkDuplicate(pickUpPointIds);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Get all routes
     * GET /api/routes
     */
    @GetMapping
    public ResponseEntity<Response> getAllRoutes() {
        Response response = routeService.findAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Get route by ID
     * GET /api/routes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getRouteById(@PathVariable String id) {
        Response response = routeService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get routes by date
     * GET /api/routes/date?date=2025-12-04T00:00:00
     */
    @GetMapping("/date")
    public ResponseEntity<Response> getRoutesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        Response response = routeService.findByRouteDate(date);
        return ResponseEntity.ok(response);
    }

    /**
     * Get routes by date range
     * GET /api/routes/range?startDate=2025-12-01T00:00:00&endDate=2025-12-31T23:59:59
     */
    @GetMapping("/range")
    public ResponseEntity<Response> getRoutesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Response response = routeService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Update route
     * PUT /api/routes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateRoute(
            @PathVariable String id,
            @RequestBody RouteUpdateDto routeUpdateDto) {
        Response response = routeService.updateById(id, routeUpdateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete route
     * DELETE /api/routes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteRoute(@PathVariable String id) {
        Response response = routeService.deleteById(id);
        return ResponseEntity.ok(response);
    }
}
