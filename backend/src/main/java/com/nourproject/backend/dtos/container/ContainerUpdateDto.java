package com.nourproject.backend.dtos.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.backend.enums.ContainerStatus;
import com.nourproject.backend.enums.ContainerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
@Builder
public class ContainerUpdateDto {
    private ContainerType containerType;
    private double capacity;
    private double fillLevel;
    private ContainerStatus containerStatus;
    private String pickUpPointId;
}
