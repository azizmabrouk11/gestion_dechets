package com.nourproject.backend.services.interfaces;

import com.nourproject.backend.dtos.Notification.NotificationDto;
import com.nourproject.backend.dtos.Notification.NotificationUpdateDto;
import com.nourproject.backend.dtos.Response;

public interface NotificationService {
    void sendEmail(NotificationDto notification);
    
    Response findAll();

    Response findById(String id);

    Response findByUserId(String userId);
    
    Response findByContainerId(String containerId);
    
    Response findByIncidentId(String incidentId);

    Response save(NotificationDto notificationDto);

    Response updateById(String id, NotificationUpdateDto notificationUpdateDto);

    Response deleteById(String id);
}
