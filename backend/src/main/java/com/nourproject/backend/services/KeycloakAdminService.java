package com.nourproject.backend.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Keycloak Admin Service
 * 
 * Provides administrative operations for Keycloak user management.
 * Handles user synchronization, updates, and deletion.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@Service
@Slf4j
public class KeycloakAdminService {

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.target-realm}")
    private String targetRealm;

    private Keycloak keycloak;
    private RealmResource realmResource;

    /**
     * Initialize Keycloak admin client after bean construction
     */
    @PostConstruct
    public void initKeycloak() {
        log.info("Initializing Keycloak Admin Client");
        log.debug("Server URL: {}, Admin Realm: {}, Target Realm: {}", serverUrl, adminRealm, targetRealm);
        
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
        
        this.realmResource = keycloak.realm(targetRealm);
        log.info("Keycloak Admin Client initialized successfully");
    }

    /**
     * Update user profile information in Keycloak
     * 
     * @param username the username
     * @param firstName the first name
     * @param lastName the last name
     * @return true if update was successful
     */
    public boolean updateUserCompleteProfile(String username, String firstName, String lastName) {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                log.error("User not found in Keycloak: {}", username);
                return false;
            }
            
            UserRepresentation user = users.get(0);
            String userId = user.getId();
            
            user.setFirstName(firstName);
            user.setLastName(lastName);
            
            UserResource userResource = usersResource.get(userId);
            userResource.update(user);
            
            log.info("Successfully updated user profile in Keycloak: {}", username);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update user profile in Keycloak: {}", username, e);
            return false;
        }
    }

    /**
     * Find a user by email address
     * 
     * @param email the email address
     * @return UserRepresentation or null if not found
     */
    public UserRepresentation findUserByEmail(String email) {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.searchByEmail(email, true);
            
            if (users.isEmpty()) {
                log.warn("User not found in Keycloak with email: {}", email);
                return null;
            }
            
            return users.get(0);
            
        } catch (Exception e) {
            log.error("Failed to find user by email in Keycloak: {}", email, e);
            return null;
        }
    }

    /**
     * Delete a user from Keycloak by email address
     * 
     * @param email the email address
     * @return true if deletion was successful
     */
    public boolean deleteUserByEmail(String email) {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.searchByEmail(email, true);
            
            if (users.isEmpty()) {
                log.warn("User not found in Keycloak with email: {}", email);
                return false;
            }
            
            UserRepresentation user = users.get(0);
            String userId = user.getId();
            String username = user.getUsername();
            
            UserResource userResource = usersResource.get(userId);
            userResource.remove();
            
            log.info("Successfully deleted user from Keycloak - Email: {}, Username: {}, ID: {}", 
                email, username, userId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to delete user from Keycloak with email: {}", email, e);
            return false;
        }
    }

    /**
     * Delete a user from Keycloak by username
     * 
     * @param username the username
     * @return true if deletion was successful
     */
    public boolean deleteUserByUsername(String username) {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                log.warn("User not found in Keycloak with username: {}", username);
                return false;
            }
            
            UserRepresentation user = users.get(0);
            String userId = user.getId();
            String email = user.getEmail();
            
            UserResource userResource = usersResource.get(userId);
            userResource.remove();
            
            log.info("Successfully deleted user from Keycloak - Username: {}, Email: {}, ID: {}", 
                username, email, userId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to delete user from Keycloak with username: {}", username, e);
            return false;
        }
    }

    /**
     * Get all users from Keycloak realm
     * Used for synchronization operations
     * 
     * @return List of UserRepresentation from Keycloak
     */
    public List<UserRepresentation> getAllKeycloakUsers() {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.list();
            
            log.info("Retrieved {} users from Keycloak", users.size());
            return users;
            
        } catch (Exception e) {
            log.error("Failed to retrieve users from Keycloak", e);
            return List.of();
        }
    }

    /**
     * Get realm roles for a specific user
     * 
     * @param userId the Keycloak user ID
     * @return List of role names
     */
    public List<String> getUserRealmRoles(String userId) {
        try {
            UserResource userResource = realmResource.users().get(userId);
            List<String> roles = userResource.roles().realmLevel().listEffective()
                    .stream()
                    .map(role -> role.getName())
                    .toList();
            
            log.debug("Retrieved {} roles for user ID: {}", roles.size(), userId);
            return roles;
            
        } catch (Exception e) {
            log.error("Failed to get user roles for user ID: {}", userId, e);
            return List.of();
        }
    }

    /**
     * Create a new user in Keycloak
     * 
     * @param username the username
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     * @param temporaryPassword the temporary password
     * @return true if user was created successfully
     */
    public boolean createUserInKeycloak(String username, String email, String firstName, String lastName, String temporaryPassword) {
        try {
            UsersResource usersResource = realmResource.users();
            
            // Check if user already exists by username
            List<UserRepresentation> existingUsersByUsername = usersResource.search(username, true);
            if (!existingUsersByUsername.isEmpty()) {
                log.warn("User already exists in Keycloak with username: {}", username);
                return false;
            }
            
            // Check if user already exists by email
            List<UserRepresentation> existingUsersByEmail = usersResource.searchByEmail(email, true);
            if (!existingUsersByEmail.isEmpty()) {
                log.warn("User already exists in Keycloak with email: {}", email);
                return false;
            }
            
            // Create new user representation
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);
            
            // Create user
            try {
                usersResource.create(user);
                log.info("Successfully created user in Keycloak: {}", username);
                
                // Set temporary password if provided
                if (temporaryPassword != null && !temporaryPassword.trim().isEmpty()) {
                    // Find the newly created user to get their ID
                    List<UserRepresentation> createdUsers = usersResource.search(username, true);
                    if (!createdUsers.isEmpty()) {
                        String userId = createdUsers.get(0).getId();
                        setUserPassword(userId, temporaryPassword, true);
                    }
                }
                
                return true;
            } catch (Exception createEx) {
                log.error("Failed to create user in Keycloak: {}", username, createEx);
                return false;
            }
            
        } catch (Exception e) {
            log.error("Exception while creating user in Keycloak: {}", username, e);
            return false;
        }
    }
    
    /**
     * Set user password in Keycloak
     * 
     * @param userId the Keycloak user ID
     * @param password the new password
     * @param temporary whether the password is temporary
     * @return true if password was set successfully
     */
    private boolean setUserPassword(String userId, String password, boolean temporary) {
        try {
            UserResource userResource = realmResource.users().get(userId);
            org.keycloak.representations.idm.CredentialRepresentation credential = new org.keycloak.representations.idm.CredentialRepresentation();
            credential.setType(org.keycloak.representations.idm.CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(temporary);
            
            userResource.resetPassword(credential);
            log.info("Successfully set password for user ID: {}", userId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to set password for user ID: {}", userId, e);
            return false;
        }
    }
}

