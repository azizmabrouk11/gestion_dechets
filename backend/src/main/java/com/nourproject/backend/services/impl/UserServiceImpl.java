package com.nourproject.backend.services.impl;

import com.mongodb.DuplicateKeyException;
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
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    public Response findAll() {
        List<UserDto> list = this.userRepository.findAll().stream()
                .map(userMapper::userToUserDto).toList();

        return Response.builder()
                .status(200)
                .message("List of users retrieved successfully")
                .users(list)
                .build();
    }

    public Response findById(String id) {
        UserDto userDto = this.userRepository.findById(id).map(userMapper::userToUserDto)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
        return Response.builder()
                .status(200)
                .message("User retrieved successfully")
                .user(userDto)
                .build();
    }

    public Response save(UserDto userDto) {
        // Create user in Keycloak first
        String temporaryPassword = "ChangeMe123!"; // Default temporary password
        boolean keycloakSuccess = keycloakAdminService.createUserInKeycloak(
            userDto.getUserName(),
            userDto.getEmail(),
            userDto.getFirstName(),
            userDto.getLastName(),
            temporaryPassword
        );
        
        if (!keycloakSuccess) {
            log.warn("Failed to create user in Keycloak, but proceeding with database save: {}", userDto.getUserName());
        }
        
        // Save user in database
        UserDto user = userMapper.userToUserDto(userRepository.save(this.userMapper.userDtoToUser(userDto)));
        return Response.builder()
                .status(200)
                .message("User saved successfully" + (keycloakSuccess ? " and synced to Keycloak" : ""))
                .user(user)
                .build();
    }

    public Response findByUserName(String userName) {
        UserDto userDto = this.userRepository.findByUserName(userName.trim()).map(userMapper::userToUserDto)
                .orElseThrow(() -> new NotFoundException("User with userName " + userName + " not found"));
        return Response.builder()
                .status(200)
                .message("User retrieved successfully")
                .user(userDto)
                .build();
    }

    public User getByUserName(String userName) {
        return this.userRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("User with userName " + userName + " not found"));
    }

    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }

    public Response findByEmail(String email) {
        UserDto userDto = this.userRepository.findByEmail(email).map(userMapper::userToUserDto)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
        return Response.builder()
                .status(200)
                .message("User retrieved successfully")
                .user(userDto)
                .build();
    }

    public Response createOrUpdateUser(UserDto userDto) {
        // Use email as primary identifier for finding existing users
        User existingUser = null;

        // First try to find by email (primary identifier)
        if (userDto.getEmail() != null && !userDto.getEmail().trim().isEmpty()) {
            existingUser = this.userRepository.findByEmail(userDto.getEmail()).orElse(null);
        }

        // If not found by email and we have a username, try by username as fallback
        if (existingUser == null && userDto.getUserName() != null && !userDto.getUserName().trim().isEmpty()) {
            existingUser = this.userRepository.findByUserName(userDto.getUserName()).orElse(null);
        }

        if (existingUser != null) {
            // User exists, update them
            UserUpdateDto updateDto = userMapper.userDtoToUserUpdateDto(userDto);
            return updateById(existingUser.getId(), updateDto);
        } else {
            // User doesn't exist, create new one
            return save(userDto);
        }
    }

    public Response updateById(String id, UserUpdateDto userUpdateDto) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("User with ID " + id + " not found"));
        this.userMapper.updateUserUpdateDtoToUser(userUpdateDto, user);
        UserDto updatedUser = this.userMapper.userToUserDto(this.userRepository.save(user));

        keycloakAdminService.updateUserCompleteProfile(
                updatedUser.getUserName(),
                updatedUser.getFirstName(),
                updatedUser.getLastName()
        );

        return Response.builder()
                .status(200)
                .message("User updated successfully")
                .user(updatedUser)
                .build();
    }

    @Transactional
    public Response deleteByUserId(String id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("User with ID " + id + " not found"));

        // Store user data for response and Keycloak deletion
        UserDto deletedUserDto = userMapper.userToUserDto(user);
        String userEmail = user.getEmail();
        String username = user.getUserName();

        // Delete from local database first
        this.userRepository.delete(user);

        // Then delete from Keycloak using email
        try {
            System.out.println("Attempting to delete user from Keycloak with email: " + userEmail);
            boolean keycloakDeleteSuccess = keycloakAdminService.deleteUserByEmail(userEmail);

            if (keycloakDeleteSuccess) {
                System.out.println("Successfully deleted user from both database and Keycloak: " + username);
            } else {
                System.err.println("User deleted from database but failed to delete from Keycloak: " + username);
            }
        } catch (Exception e) {
            System.err.println("Error deleting user from Keycloak: " + e.getMessage());
        }

        return Response.builder()
                .status(200)
                .message("User deleted successfully")
                .user(deletedUserDto)
                .build();
    }
}
