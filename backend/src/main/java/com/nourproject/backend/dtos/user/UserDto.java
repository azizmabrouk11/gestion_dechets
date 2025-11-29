package com.nourproject.backend.dtos.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Data Transfer Object
 * 
 * Used for transferring user data between layers.
 * Excludes sensitive information and internal fields.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    
    /**
     * User ID (MongoDB document ID)
     */
    private String id;
    
    /**
     * Username - Required field
     */
    @NotBlank(message = "Username is required")
    private String userName;
    
    /**
     * Email address - Required and must be valid
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    /**
     * First name
     */
    private String firstName;
    
    /**
     * Last name
     */
    private String lastName;
    
    /**
     * User role - defaults to User
     */
    @Builder.Default
    private UserRole role = UserRole.User;
    
    /**
     * Account active status
     */
    @Builder.Default
    private Boolean isActive = false;
    
    /**
     * Profile image (Base64 or URL)
     */
    private String profileImage;
    
    /**
     * Face authentication enabled flag
     */
    @Builder.Default
    private Boolean faceAuthEnabled = false;
    
    /**
     * Account creation timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;
}
