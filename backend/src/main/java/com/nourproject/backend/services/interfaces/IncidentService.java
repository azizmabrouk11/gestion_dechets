package com.nourproject.backend.services.interfaces;

import com.nourproject.backend.dtos.incident.IncidentDto;
import com.nourproject.backend.dtos.incident.IncidentUpdateDto;
import com.nourproject.backend.enums.IncidentPriority;
import com.nourproject.backend.enums.IncidentStatus;
import com.nourproject.backend.dtos.Response;

public interface IncidentService {

    Response findAll();

    Response findById(String id);

    Response findByStatus(IncidentStatus status);

    Response findByCitizenId(String citizenId);

    Response findByPickUpPointId(String pickUpPointId);

    Response findByPriority(IncidentPriority priority);

    Response findByAssignedTo(String assignedTo);

    Response save(IncidentDto incidentDto);

    Response updateById(String id, IncidentUpdateDto incidentUpdateDto);

    Response deleteById(String id);
}
