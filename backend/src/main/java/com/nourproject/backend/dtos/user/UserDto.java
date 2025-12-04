package com.nourproject.backend.dtos.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.backend.enums.VehiculeType;
import com.nourproject.backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {


    private String id;

    @NotBlank(message = "Username is required")
    private String userName;


    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;


    private String firstName;


    private String lastName;


    @Builder.Default
    private UserRole role = UserRole.user;


    @Builder.Default
    private Boolean isActive = false;


    private String profileImage;


    @Builder.Default
    private Boolean faceAuthEnabled = false;

    private List<VehiculeType> vehicules ;


    private Boolean driver ;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;







}
