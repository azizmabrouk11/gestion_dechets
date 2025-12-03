package com.nourproject.backend.entities;

import com.nourproject.backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String _id;
    private String subject;
    private String recipient;
    private String body;
    private NotificationType notificationType;
    @Field("userId")
    private String userId;

    @Field("containerId")     // or "container_id" if you prefer snake_case in DB
    private String containerId;

    @Field("incidentId")
    private String incidentId;

}
