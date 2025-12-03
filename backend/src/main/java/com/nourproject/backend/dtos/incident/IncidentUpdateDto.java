package com.nourproject.backend.dtos.incident;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.backend.enums.IncidentPriority;
import com.nourproject.backend.enums.IncidentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncidentUpdateDto {

    private String description;
    private String location;
    private IncidentStatus status;
    private IncidentPriority priority;
    private String pickUpPointId;
    private String imageUrl;
    private LocalDateTime resolvedAt;
    private String assignedTo;
    private String resolutionNotes;
}
