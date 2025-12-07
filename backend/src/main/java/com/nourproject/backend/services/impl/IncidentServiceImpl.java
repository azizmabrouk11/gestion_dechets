package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.incident.IncidentDto;
import com.nourproject.backend.dtos.incident.IncidentUpdateDto;
import com.nourproject.backend.entities.Incident;
import com.nourproject.backend.enums.IncidentPriority;
import com.nourproject.backend.enums.IncidentStatus;
import com.nourproject.backend.mappers.IncidentMapper;
import com.nourproject.backend.repositories.IncidentRepository;
import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.services.interfaces.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;

    @Override
    public Response findAll() {
        List<IncidentDto> incidents = incidentRepository.findAll()
                .stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incidents retrieved successfully")
                .incidents(incidents)
                .build();
    }

    @Override
    public Response findById(String id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incident found")
                .incident(incidentMapper.toDto(incident))
                .build();
    }

    @Override
    public Response findByStatus(IncidentStatus status) {
        List<IncidentDto> incidents = incidentRepository.findByStatus(status)
                .stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incidents retrieved by status")
                .incidents(incidents)
                .build();
    }

    @Override
    public Response findByCitizenId(String citizenId) {
        List<IncidentDto> incidents = incidentRepository.findByCitizenId(citizenId)
                .stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incidents retrieved for citizen")
                .incidents(incidents)
                .build();
    }

    @Override
    public Response findByPickUpPointId(String pickUpPointId) {
        List<IncidentDto> incidents = incidentRepository.findByPickUpPointId(pickUpPointId)
                .stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incidents retrieved for pickup point")
                .incidents(incidents)
                .build();
    }

    @Override
    public Response findByPriority(IncidentPriority priority) {
        List<IncidentDto> incidents = incidentRepository.findByPriority(priority)
                .stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incidents retrieved by priority")
                .incidents(incidents)
                .build();
    }

    @Override
    public Response findByAssignedTo(String assignedTo) {
        List<IncidentDto> incidents = incidentRepository.findByAssignedTo(assignedTo)
                .stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incidents retrieved for assigned user")
                .incidents(incidents)
                .build();
    }

    @Override
    public Response save(IncidentDto incidentDto) {
        Incident incident = incidentMapper.toEntity(incidentDto);

        if (incident.getStatus() == null) {
            incident.setStatus(IncidentStatus.reported);
        }

        Incident savedIncident = incidentRepository.save(incident);

        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("Incident created successfully")
                .incident(incidentMapper.toDto(savedIncident))
                .build();
    }

    @Override
    public Response updateById(String id, IncidentUpdateDto incidentUpdateDto) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        incidentMapper.updateIncidentFromDto(incidentUpdateDto, incident);

        if (incidentUpdateDto.getStatus() == IncidentStatus.resolved && incident.getResolvedAt() == null) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        Incident updatedIncident = incidentRepository.save(incident);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incident updated successfully")
                .incident(incidentMapper.toDto(updatedIncident))
                .build();
    }

    @Override
    public Response deleteById(String id) {
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incident not found with id: " + id);
        }

        incidentRepository.deleteById(id);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Incident deleted successfully")
                .build();
    }
}
