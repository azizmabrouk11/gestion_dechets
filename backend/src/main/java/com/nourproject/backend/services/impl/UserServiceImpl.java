package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.user.UserUpdateDto;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.exceptions.GlobalException;
import com.nourproject.backend.exceptions.NotFoundException;
import com.nourproject.backend.mappers.UserMapper;
import com.nourproject.backend.repositories.UserRepository;
import com.nourproject.backend.services.KeycloakAdminService;
import com.nourproject.backend.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service Implementation
 * 
 * Implements business logic for user management operations.
 * Handles CRUD operations, validation, and data transformation.
 * Synchronizes with Keycloak for authentication management.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakAdminService keycloakAdminService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Response findAll() {
        log.info("Fetching all users");
        try {
            List<User> users = userRepository.findAll();
            List<UserDto> userDtos = users.stream()
                    .map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
            
            log.info("Successfully retrieved {} users", userDtos.size());
            return Response.builder()
                    .status(200)
                    .message("Users retrieved successfully")
                    .userList(userDtos)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching all users", e);
            throw new GlobalException("Error retrieving users: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User getByUserName(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new NotFoundException("User not found with username: " + username);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new NotFoundException("User not found with email: " + email);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Response findById(String id) {
        log.info("Fetching user by ID: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
            
            UserDto userDto = userMapper.userToUserDto(user);
            
            log.info("Successfully retrieved user: {}", user.getUserName());
            return Response.builder()
                    .status(200)
                    .message("User retrieved successfully")
                    .user(userDto)
                    .build();
        } catch (NotFoundException e) {
            log.error("User not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user by ID: {}", id, e);
            throw new GlobalException("Error retrieving user: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Response findByUserName(String username) {
        log.info("Fetching user by username: {}", username);
        try {
            User user = getByUserName(username);
            UserDto userDto = userMapper.userToUserDto(user);
            
            return Response.builder()
                    .status(200)
                    .message("User retrieved successfully")
                    .user(userDto)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user by username: {}", username, e);
            throw new GlobalException("Error retrieving user: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Response findByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        try {
            User user = getByEmail(email);
            UserDto userDto = userMapper.userToUserDto(user);
            
            return Response.builder()
                    .status(200)
                    .message("User retrieved successfully")
                    .user(userDto)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user by email: {}", email, e);
            throw new GlobalException("Error retrieving user: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response save(UserDto userDto) {
        log.info("Creating new user: {}", userDto.getUserName());
        try {
            // Validate unique constraints
            if (userRepository.existsByUserName(userDto.getUserName())) {
                log.error("Username already exists: {}", userDto.getUserName());
                throw new GlobalException("Username already exists: " + userDto.getUserName());
            }
            
            if (userRepository.existsByEmail(userDto.getEmail())) {
                log.error("Email already exists: {}", userDto.getEmail());
                throw new GlobalException("Email already exists: " + userDto.getEmail());
            }
            
            // Map DTO to entity
            User user = userMapper.userDtoToUser(userDto);
            user.setCreatedAt(LocalDateTime.now());
            
            // Save user
            User savedUser = userRepository.save(user);
            UserDto savedUserDto = userMapper.userToUserDto(savedUser);
            
            log.info("Successfully created user: {}", savedUser.getUserName());
            return Response.builder()
                    .status(201)
                    .message("User created successfully")
                    .user(savedUserDto)
                    .build();
        } catch (DuplicateKeyException e) {
            log.error("Duplicate key error creating user", e);
            throw new GlobalException("User with this username or email already exists");
        } catch (Exception e) {
            log.error("Error creating user: {}", userDto.getUserName(), e);
            throw new GlobalException("Error creating user: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response updateById(String id, UserUpdateDto userUpdateDto) {
        log.info("Updating user with ID: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
            
            // Update only non-null fields
            userMapper.updateUserUpdateDtoToUser(userUpdateDto, user);
            user.setUpdatedAt(LocalDateTime.now());
            
            // Save updated user
            User updatedUser = userRepository.save(user);
            UserDto updatedUserDto = userMapper.userToUserDto(updatedUser);
            
            log.info("Successfully updated user: {}", updatedUser.getUserName());
            return Response.builder()
                    .status(200)
                    .message("User updated successfully")
                    .user(updatedUserDto)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating user with ID: {}", id, e);
            throw new GlobalException("Error updating user: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * Also deletes the user from Keycloak using email as identifier
     */
    @Override
    public Response deleteByUserId(String id) {
        log.info("Deleting user with ID: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
            
            String username = user.getUserName();
            String email = user.getEmail();
            
            // Delete from MongoDB first
            userRepository.deleteById(id);
            log.info("Successfully deleted user from database: {} ({})", username, email);
            
            // Try to delete from Keycloak using email (primary identifier)
            try {
                if (email != null && !email.trim().isEmpty()) {
                    boolean keycloakDeleted = keycloakAdminService.deleteUserByEmail(email);
                    if (keycloakDeleted) {
                        log.info("Successfully deleted user from Keycloak by email: {}", email);
                    } else {
                        log.warn("Failed to delete user from Keycloak by email: {} - User may not exist in Keycloak", email);
                    }
                } else {
                    log.warn("User has no email, cannot delete from Keycloak: {}", username);
                }
            } catch (Exception keycloakError) {
                log.error("Error deleting user from Keycloak (email: {}): {}", email, keycloakError.getMessage());
                // Don't fail the entire operation if Keycloak deletion fails
                // The user is already deleted from database
            }
            
            return Response.builder()
                    .status(200)
                    .message("User deleted successfully from database and Keycloak")
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}", id, e);
            throw new GlobalException("Error deleting user: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response createOrUpdateUser(UserDto userDto) {
        log.info("Creating or updating user: {}", userDto.getEmail());
        try {
            // Check if user exists by email
            return userRepository.findByEmail(userDto.getEmail())
                    .map(existingUser -> {
                        log.info("User exists, updating: {}", userDto.getEmail());
                        // User exists, update it
                        UserUpdateDto updateDto = userMapper.userDtoToUserUpdateDto(userDto);
                        userMapper.updateUserUpdateDtoToUser(updateDto, existingUser);
                        existingUser.setUpdatedAt(LocalDateTime.now());
                        
                        User updatedUser = userRepository.save(existingUser);
                        UserDto updatedUserDto = userMapper.userToUserDto(updatedUser);
                        
                        return Response.builder()
                                .status(200)
                                .message("User synchronized successfully")
                                .user(updatedUserDto)
                                .build();
                    })
                    .orElseGet(() -> {
                        log.info("User does not exist, creating: {}", userDto.getEmail());
                        // User doesn't exist, create new
                        return save(userDto);
                    });
        } catch (Exception e) {
            log.error("Error in createOrUpdateUser for email: {}", userDto.getEmail(), e);
            throw new GlobalException("Error synchronizing user: " + e.getMessage());
        }
    }
}
