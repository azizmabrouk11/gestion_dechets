package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.Incident;
import com.nourproject.backend.enums.IncidentPriority;
import com.nourproject.backend.enums.IncidentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IncidentRepository extends MongoRepository<Incident, String> {
    
    List<Incident> findByStatus(IncidentStatus status);
    
    List<Incident> findByCitizenId(String citizenId);
    
    List<Incident> findByPickUpPointId(String pickUpPointId);
    
    List<Incident> findByPriority(IncidentPriority priority);
    
    List<Incident> findByAssignedTo(String assignedTo);
}
