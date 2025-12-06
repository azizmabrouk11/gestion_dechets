package com.nourproject.backend.dtos.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteInstructionDTO {
    private Double distance;
    private Integer sign;
    private String text;
    private Long time;
    private String streetName;
}
