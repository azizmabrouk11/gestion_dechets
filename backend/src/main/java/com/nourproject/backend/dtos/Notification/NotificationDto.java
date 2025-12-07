package com.nourproject.backend.dtos.Notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto {
    private String id;
    private String subject;
    private String recipient;
    private String body;
    private NotificationType notificationType;
    private String userId;
    private String containerId;
    private String incidentId;
    private LocalDateTime date;
}
