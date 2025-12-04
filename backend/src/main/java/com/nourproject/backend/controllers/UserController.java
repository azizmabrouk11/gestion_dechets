package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.user.UserDto;
import com.nourproject.backend.dtos.user.UserUpdateDto;
import com.nourproject.backend.entities.User;
import com.nourproject.backend.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<Response> getUsers() {
        return ResponseEntity.ok(this.userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable(value = "id", required = true) String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Response> getUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(this.userService.findByUserName(username));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Response> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(this.userService.findByEmail(email));
    }

    @PostMapping()
    public ResponseEntity<Response> createOrUpdateUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(this.userService.createOrUpdateUser(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUserById(
            @PathVariable(value = "id", required = true) String id,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateById(id, userUpdateDto));
    }

    @PutMapping("/username/{username}")
    public ResponseEntity<Response> updateUserByUsername(
            @PathVariable("username") String username,
            @RequestBody UserUpdateDto userUpdateDto) {
        User user = this.userService.getByUserName(username);
        return ResponseEntity.ok(this.userService.updateById(user.getId(), userUpdateDto));
    }

    @PutMapping("/email/{email}")
    public ResponseEntity<Response> updateUserByEmail(
            @PathVariable("email") String email,
            @RequestBody UserUpdateDto userUpdateDto) {
        User user = this.userService.getByEmail(email);
        return ResponseEntity.ok(this.userService.updateById(user.getId(), userUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUserById(@PathVariable(value = "id", required = true) String id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.deleteByUserId(id));
    }
}
