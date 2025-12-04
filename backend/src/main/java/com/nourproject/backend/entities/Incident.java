package com.nourproject.backend.entities;

import com.nourproject.backend.enums.IncidentPriority;
import com.nourproject.backend.enums.IncidentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incidents")
public class Incident {
    @Id
    private String _id;

    private String description;
    private String location;
    private IncidentStatus status;
    private IncidentPriority priority;

    private String citizenId;
    private String pickUpPointId;

    private String imageUrl;


    private LocalDateTime resolvedAt;

    private String assignedTo;
    private String resolutionNotes;

    @Builder.Default
    private LocalDateTime date=LocalDateTime.now() ;
}
