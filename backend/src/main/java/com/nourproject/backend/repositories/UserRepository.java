package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.User;
import com.nourproject.backend.enums.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository Interface
 * 
 * Provides data access methods for User entities using MongoDB.
 * Extends MongoRepository to inherit standard CRUD operations.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find a user by username
     * 
     * @param userName the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUserName(String userName);
    
    /**
     * Find a user by email address
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a username already exists in the database
     * 
     * @param userName the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUserName(String userName);
    
    /**
     * Check if an email already exists in the database
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all users with a specific role
     * 
     * @param role the user role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find all active users
     * 
     * @param isActive the active status to filter by
     * @return List of users with the specified active status
     */
    List<User> findByIsActive(Boolean isActive);
    
    /**
     * Find users with face authentication enabled
     * 
     * @param faceAuthEnabled the face auth status to filter by
     * @return List of users with face authentication enabled
     */
    List<User> findByFaceAuthEnabled(Boolean faceAuthEnabled);
    
    /**
     * Find users by first name or last name (case-insensitive)
     * 
     * @param firstName the first name to search for
     * @param lastName the last name to search for
     * @return List of users matching the name criteria
     */
    @Query("{ $or: [ { 'first_name': { $regex: ?0, $options: 'i' } }, { 'last_name': { $regex: ?1, $options: 'i' } } ] }")
    List<User> findByFirstNameOrLastName(String firstName, String lastName);
    
    /**
     * Delete a user by username
     * 
     * @param userName the username of the user to delete
     */
    void deleteByUserName(String userName);
    
    /**
     * Delete a user by email
     * 
     * @param email the email of the user to delete
     */
    void deleteByEmail(String email);
}
