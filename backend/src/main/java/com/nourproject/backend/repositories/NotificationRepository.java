package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId);

    // This works 100% reliably and fast
    List<Notification> findByContainerId(String containerId);

    List<Notification> findByIncidentId(String incidentId);
}
