package com.nourproject.backend.dtos.vehicule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.enums.UserRole;
import com.nourproject.backend.enums.VehiculeStatus;
import com.nourproject.backend.enums.VehiculeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class VehiculeUpdateDto {

    private String matricul;
    private Double capacity;
    private VehiculeStatus vehiculeStatus;
    private VehiculeType vehiculeType;
    List<User> users;

}
