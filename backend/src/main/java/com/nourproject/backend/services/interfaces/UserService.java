package com.nourproject.backend.services.interfaces;

import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.user.UserUpdateDto;
import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.entities.User;

/**
 * User Service Interface
 * 
 * Defines business logic operations for user management.
 * Follows the service layer pattern for clean architecture.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
public interface UserService {
    
    /**
     * Retrieve all users from the system
     * 
     * @return Response containing list of all users
     */
    Response findAll();
    
    /**
     * Get user entity by username (for internal use)
     * 
     * @param username the username to search for
     * @return User entity
     */
    User getByUserName(String username);
    
    /**
     * Get user entity by email (for internal use)
     * 
     * @param email the email to search for
     * @return User entity
     */
    User getByEmail(String email);
    
    /**
     * Find user by ID
     * 
     * @param id the user ID
     * @return Response containing user data
     */
    Response findById(String id);
    
    /**
     * Find user by username
     * 
     * @param username the username to search for
     * @return Response containing user data
     */
    Response findByUserName(String username);
    
    /**
     * Find user by email
     * 
     * @param email the email to search for
     * @return Response containing user data
     */
    Response findByEmail(String email);
    
    /**
     * Create a new user
     * 
     * @param userDto the user data transfer object
     * @return Response containing created user data
     */
    Response save(UserDto userDto);
    
    /**
     * Update an existing user by ID
     * 
     * @param id the user ID
     * @param userUpdateDto the update data transfer object
     * @return Response containing updated user data
     */
    Response updateById(String id, UserUpdateDto userUpdateDto);
    
    /**
     * Delete a user by ID
     * 
     * @param id the user ID
     * @return Response confirming deletion
     */
    Response deleteByUserId(String id);
    
    /**
     * Create a new user or update existing user based on email
     * Synchronizes user data from Keycloak or external sources
     * 
     * @param userDto the user data transfer object
     * @return Response containing user data
     */
    Response createOrUpdateUser(UserDto userDto);
}
