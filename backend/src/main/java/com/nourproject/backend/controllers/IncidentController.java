package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.incident.IncidentDto;
import com.nourproject.backend.dtos.incident.IncidentUpdateDto;
import com.nourproject.backend.enums.IncidentPriority;
import com.nourproject.backend.enums.IncidentStatus;
import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.services.interfaces.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public ResponseEntity<Response> getAllIncidents() {
        return ResponseEntity.ok(incidentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getIncidentById(@PathVariable String id) {
        return ResponseEntity.ok(incidentService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Response> getIncidentsByStatus(@PathVariable IncidentStatus status) {
        return ResponseEntity.ok(incidentService.findByStatus(status));
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<Response> getIncidentsByCitizenId(@PathVariable String citizenId) {
        return ResponseEntity.ok(incidentService.findByCitizenId(citizenId));
    }

    @GetMapping("/pickuppoint/{pickUpPointId}")
    public ResponseEntity<Response> getIncidentsByPickUpPointId(@PathVariable String pickUpPointId) {
        return ResponseEntity.ok(incidentService.findByPickUpPointId(pickUpPointId));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<Response> getIncidentsByPriority(@PathVariable IncidentPriority priority) {
        return ResponseEntity.ok(incidentService.findByPriority(priority));
    }

    @GetMapping("/assigned/{assignedTo}")
    public ResponseEntity<Response> getIncidentsByAssignedTo(@PathVariable String assignedTo) {
        return ResponseEntity.ok(incidentService.findByAssignedTo(assignedTo));
    }

    @PostMapping
    public ResponseEntity<Response> createIncident(@RequestBody IncidentDto incidentDto) {
        return ResponseEntity.ok(incidentService.save(incidentDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateIncident(@PathVariable String id, @RequestBody IncidentUpdateDto incidentUpdateDto) {
        return ResponseEntity.ok(incidentService.updateById(id, incidentUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteIncident(@PathVariable String id) {
        return ResponseEntity.ok(incidentService.deleteById(id));
    }
}
