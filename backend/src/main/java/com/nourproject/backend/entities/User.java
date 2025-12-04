package com.nourproject.backend.entities;

import com.nourproject.backend.enums.VehiculeType;
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
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotBlank(message = "Username is required")
    @Indexed(unique = true)
    @Field("user_name")
    private String userName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Indexed(unique = true)
    @Field("email")
    private String email;
    @Field("first_name")
    private String firstName;
    @Field("last_name")
    private String lastName;
    @Builder.Default
    @Field("role")
    private UserRole role = UserRole.user;
    @Builder.Default
    @Field("is_active")
    private Boolean isActive = false;
    @Field("profile_image")
    private String profileImage;
    @Builder.Default
    @Field("face_auth_enabled")
    private Boolean faceAuthEnabled = false;
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;


    @Builder.Default
    @Field("vehicules")
    private List<VehiculeType> vehicules = new ArrayList<>();


    @Builder.Default
    @Field("driver")
    private Boolean driver = false;



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
