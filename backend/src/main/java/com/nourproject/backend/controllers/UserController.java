package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.user.UserUpdateDto;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 *
 * RESTful API endpoints for user management operations.
 * Handles CRUD operations and user data retrieval.
 *
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@RestController
@RequestMapping("/api/public/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get all users
     *
     * @return ResponseEntity with list of users
     */
    @GetMapping
    public ResponseEntity<Response> getAllUsers() {
        log.info("GET /api/public/users - Fetching all users");
        Response response = userService.findAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID
     *
     * @param id the user ID (MongoDB String ID)
     * @return ResponseEntity with user data
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable String id) {
        log.info("GET /api/public/users/{} - Fetching user by ID", id);
        Response response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by username
     *
     * @param username the username
     * @return ResponseEntity with user data
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Response> getUserByUsername(@PathVariable String username) {
        log.info("GET /api/public/users/username/{} - Fetching user by username", username);
        Response response = userService.findByUserName(username);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by email
     *
     * @param email the email address
     * @return ResponseEntity with user data
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Response> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/public/users/email/{} - Fetching user by email", email);
        Response response = userService.findByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Create or update user (synchronization endpoint)
     * Used by Keycloak integration to sync user data
     *
     * @param userDto the user data
     * @return ResponseEntity with user data
     */
    @PostMapping
    public ResponseEntity<Response> createOrUpdateUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /api/public/users - Creating or updating user: {}", userDto.getEmail());
        Response response = userService.createOrUpdateUser(userDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Update user by ID
     *
     * @param id the user ID
     * @param userUpdateDto the update data
     * @return ResponseEntity with updated user data
     */
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUserById(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("PUT /api/public/users/{} - Updating user", id);
        Response response = userService.updateById(id, userUpdateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user by username
     *
     * @param username the username
     * @param userUpdateDto the update data
     * @return ResponseEntity with updated user data
     */
    @PutMapping("/username/{username}")
    public ResponseEntity<Response> updateUserByUsername(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("PUT /api/public/users/username/{} - Updating user by username", username);
        User user = userService.getByUserName(username);
        Response response = userService.updateById(user.getId(), userUpdateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user by email
     *
     * @param email the email address
     * @param userUpdateDto the update data
     * @return ResponseEntity with updated user data
     */
    @PutMapping("/email/{email}")
    public ResponseEntity<Response> updateUserByEmail(
            @PathVariable String email,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("PUT /api/public/users/email/{} - Updating user by email", email);
        User user = userService.getByEmail(email);
        Response response = userService.updateById(user.getId(), userUpdateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user by ID
     *
     * @param id the user ID
     * @return ResponseEntity with deletion confirmation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUserById(@PathVariable String id) {
        log.info("DELETE /api/public/users/{} - Deleting user", id);
        Response response = userService.deleteByUserId(id);
        return ResponseEntity.ok(response);
    }
}
