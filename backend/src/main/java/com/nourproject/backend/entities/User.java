package com.nourproject.backend.entities;

import com.nourproject.backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * User Entity - MongoDB Document
 * 
 * Represents a user in the waste management system with authentication
 * and profile management capabilities.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    /**
     * Unique identifier for the user document
     * MongoDB generates this automatically
     */
    @Id
    private String id;
    
    /**
     * Username - Must be unique across the system
     * Used for authentication and display purposes
     */
    @NotBlank(message = "Username is required")
    @Indexed(unique = true)
    @Field("user_name")
    private String userName;
    
    /**
     * Email address - Must be unique and valid
     * Primary contact and authentication identifier
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Indexed(unique = true)
    @Field("email")
    private String email;
    
    /**
     * User's first name
     */
    @Field("first_name")
    private String firstName;
    
    /**
     * User's last name
     */
    @Field("last_name")
    private String lastName;
    
    /**
     * User role defining permissions
     * Default: User
     * @see UserRole
     */
    @Builder.Default
    @Field("role")
    private UserRole role = UserRole.User;
    
    /**
     * Account activation status
     * Default: false (requires email verification)
     */
    @Builder.Default
    @Field("is_active")
    private Boolean isActive = false;
    
    /**
     * Base64 encoded profile image or image URL
     * Stored as text to support large images
     */
    @Field("profile_image")
    private String profileImage;
    
    /**
     * Face authentication enablement flag
     * Indicates if user has registered for facial recognition login
     */
    @Builder.Default
    @Field("face_auth_enabled")
    private Boolean faceAuthEnabled = false;
    
    /**
     * Timestamp when the user account was created
     * Automatically set on document creation
     */
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user document was last modified
     * Automatically updated on document changes
     */
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Constructor for creating a user with essential fields
     * Useful for quick user creation during registration
     * 
     * @param userName the username
     * @param email the email address
     * @param firstName the first name
     * @param lastName the last name
     * @param role the user role
     */
    public User(String userName, String email, String firstName, String lastName, UserRole role) {
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = false;
        this.faceAuthEnabled = false;
        this.createdAt = LocalDateTime.now();
    }
}
