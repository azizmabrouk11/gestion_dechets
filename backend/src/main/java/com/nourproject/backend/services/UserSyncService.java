package com.nourproject.backend.services;

import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.enums.UserRole;
import com.nourproject.backend.repositories.UserRepository;
import com.nourproject.backend.services.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User Synchronization Service
 * 
 * Synchronizes users between Keycloak and the MongoDB database.
 * Runs automatically at application startup to ensure data consistency.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncService implements CommandLineRunner {

    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    /**
     * Runs at application startup to sync all Keycloak users to the database
     * Implements retry logic to wait for Keycloak availability
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("=== Starting User Synchronization Service ===");
        log.info("Waiting for Keycloak to be ready before user synchronization...");
        
        // Wait up to 60 seconds for Keycloak to be ready
        int maxRetries = 12;  // 12 * 5 seconds = 60 seconds max wait
        int retryCount = 0;
        boolean keycloakReady = false;
        
        while (retryCount < maxRetries && !keycloakReady) {
            try {
                Thread.sleep(5000);  // Wait 5 seconds before each attempt
                log.info("Attempting to connect to Keycloak (attempt {}/{})", retryCount + 1, maxRetries);
                keycloakAdminService.getAllKeycloakUsers();  // Test connection
                keycloakReady = true;
                log.info("✓ Keycloak is ready! Starting user synchronization...");
            } catch (Exception e) {
                retryCount++;
                if (retryCount < maxRetries) {
                    log.warn("Keycloak not ready yet, retrying in 5 seconds... ({}/{})", retryCount, maxRetries);
                } else {
                    log.error("✗ Keycloak not ready after {} attempts. Skipping user synchronization.", maxRetries);
                    log.error("Error: {}", e.getMessage());
                    return;
                }
            }
        }
        
        syncKeycloakUsersToDatabase();
        log.info("=== User Synchronization Service Completed ===");
    }

    /**
     * Synchronizes all users from Keycloak to the database
     * Uses email as the primary identifier for matching users
     */
    public void syncKeycloakUsersToDatabase() {
        try {
            // Get all users from Keycloak
            List<UserRepresentation> keycloakUsers = keycloakAdminService.getAllKeycloakUsers();
            
            if (keycloakUsers.isEmpty()) {
                log.warn("No users found in Keycloak or failed to retrieve users");
                return;
            }

            int syncedCount = 0;
            int skippedCount = 0;
            int errorCount = 0;

            log.info("Starting synchronization of {} Keycloak users...", keycloakUsers.size());

            for (UserRepresentation keycloakUser : keycloakUsers) {
                try {
                    boolean userSynced = syncSingleUser(keycloakUser);
                    if (userSynced) {
                        syncedCount++;
                        log.debug("✓ Synced/Updated user: {}", keycloakUser.getEmail());
                    } else {
                        skippedCount++;
                        log.debug("- Skipped user: {} (no changes)", keycloakUser.getEmail());
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.error("✗ Error syncing user {}: {}", 
                        keycloakUser.getUsername(), e.getMessage());
                }
            }

            log.info("User sync summary:");
            log.info("  ✓ Synced/Updated: {}", syncedCount);
            log.info("  - Skipped (no changes): {}", skippedCount);
            log.info("  ✗ Errors: {}", errorCount);

        } catch (Exception e) {
            log.error("Failed to sync Keycloak users to database", e);
        }
    }

    /**
     * Syncs a single user from Keycloak to the database
     * Uses email as primary identifier (more stable than username)
     * Updates existing users' roles and status if they've changed in Keycloak
     * 
     * @param keycloakUser the Keycloak user representation
     * @return true if user was synced/created/updated, false if skipped
     */
    private boolean syncSingleUser(UserRepresentation keycloakUser) {
        String email = keycloakUser.getEmail();
        
        // Skip users without email
        if (email == null || email.trim().isEmpty()) {
            log.warn("Skipping user with no email - Keycloak ID: {}, Username: {}", 
                keycloakUser.getId(), keycloakUser.getUsername());
            return false;
        }

        // Try to find by email only
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            // User exists - update their role and status if changed
            return updateUserFromKeycloak(existingUser, keycloakUser);
        }

        // User doesn't exist, create new user
        return createUserFromKeycloak(keycloakUser);
    }

    /**
     * Updates an existing user's information from Keycloak
     * Updates role, active status, first name, last name
     * 
     * @param existingUser the existing user in database
     * @param keycloakUser the Keycloak user representation
     * @return true if user was updated
     */
    private boolean updateUserFromKeycloak(User existingUser, UserRepresentation keycloakUser) {
        try {
            // Extract current role from Keycloak
            UserRole keycloakRole = extractUserRole(keycloakUser);
            boolean updated = false;

            // Update role if changed
            if (existingUser.getRole() != keycloakRole) {
                log.info("Updating role for user {} from {} to {}", 
                    existingUser.getEmail(), existingUser.getRole(), keycloakRole);
                existingUser.setRole(keycloakRole);
                updated = true;
            }

            // Update active status if changed
            if (existingUser.getIsActive() != keycloakUser.isEnabled()) {
                log.info("Updating active status for user {} from {} to {}", 
                    existingUser.getEmail(), existingUser.getIsActive(), keycloakUser.isEnabled());
                existingUser.setIsActive(keycloakUser.isEnabled());
                updated = true;
            }

            // Update first name if changed
            if (keycloakUser.getFirstName() != null && 
                !keycloakUser.getFirstName().equals(existingUser.getFirstName())) {
                existingUser.setFirstName(keycloakUser.getFirstName());
                updated = true;
            }

            // Update last name if changed
            if (keycloakUser.getLastName() != null && 
                !keycloakUser.getLastName().equals(existingUser.getLastName())) {
                existingUser.setLastName(keycloakUser.getLastName());
                updated = true;
            }

            if (updated) {
                userRepository.save(existingUser);
                log.info("Updated user from Keycloak - Email: {}, Role: {}", 
                    existingUser.getEmail(), keycloakRole);
                return true;
            } else {
                log.debug("No changes for user {}", existingUser.getEmail());
                return false;
            }

        } catch (Exception e) {
            log.error("Failed to update user from Keycloak data: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Creates a new user in the database from Keycloak user data
     * Email is used as the primary identifier
     * 
     * @param keycloakUser the Keycloak user representation
     * @return true if user was created successfully
     */
    private boolean createUserFromKeycloak(UserRepresentation keycloakUser) {
        try {
            String email = keycloakUser.getEmail();
            String username = keycloakUser.getUsername();
            
            // Ensure we have email (required)
            if (email == null || email.trim().isEmpty()) {
                log.warn("Cannot create user without email, username was: {}", username);
                return false;
            }

            // Use email as primary identifier, generate username if not available
            String finalEmail = email;
            String finalUsername = (username != null && !username.trim().isEmpty()) 
                ? username 
                : email.split("@")[0]; // Use email prefix as username if username is not available

            // Extract user role from Keycloak
            UserRole userRole = extractUserRole(keycloakUser);

            // Create UserDto for the service
            UserDto userDto = UserDto.builder()
                    .userName(finalUsername)
                    .email(finalEmail)
                    .firstName(keycloakUser.getFirstName())
                    .lastName(keycloakUser.getLastName())
                    .role(userRole)
                    .isActive(keycloakUser.isEnabled())
                    .faceAuthEnabled(false)
                    .build();

            // Create user via service
            userService.save(userDto);
            
            log.info("Created new user from Keycloak - Email: {}, Username: {}, Role: {}", 
                finalEmail, finalUsername, userRole);
            return true;

        } catch (Exception e) {
            log.error("Failed to create user from Keycloak data: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Extracts user role from Keycloak user representation
     * Checks realm roles to determine if user is admin or regular user
     * 
     * @param keycloakUser the Keycloak user
     * @return UserRole enum value
     */
    private UserRole extractUserRole(UserRepresentation keycloakUser) {
        try {
            // Get user roles from Keycloak using the admin service
            List<String> userRoles = keycloakAdminService.getUserRealmRoles(keycloakUser.getId());
            
            // Check roles in priority order (Admin > Employe > User)
            if (userRoles.contains("admin")) {
                log.debug("User {} has admin role", keycloakUser.getEmail());
                return UserRole.admin;
            }
            
            if (userRoles.contains("employe")) {
                log.debug("User {} has employe role", keycloakUser.getEmail());
                return UserRole.employe;
            }
            
            // Default to User role
            log.debug("User {} has user role (default)", keycloakUser.getEmail());
            return UserRole.user;
            
        } catch (Exception e) {
            log.warn("Failed to extract role from Keycloak user {}, defaulting to User role: {}", 
                keycloakUser.getEmail(), e.getMessage());
            return UserRole.user;
        }
    }

    /**
     * Manual sync method that can be called from controllers or other services
     * Useful for triggering sync without restarting the application
     */
    public void manualSync() {
        log.info("=== Manual User Synchronization Triggered ===");
        syncKeycloakUsersToDatabase();
        log.info("=== Manual User Synchronization Completed ===");
    }
}
