package com.oma.controller;

import com.oma.dto.UserDto;
import com.oma.dto.UserMapper;
import com.oma.model.User;
import com.oma.repository.UserRepo;
import com.oma.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, UserRepo userRepo) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Get all users (only Admins)")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDTOs = users.stream()
                .map(UserMapper::toDTO)
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Get user by id (only Admins)")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(	summary = "Register a user (Anyone)")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok("Success");
    }

    @PutMapping("/{id}")
    @Operation(	summary = "Update user by id")
    public UserDto updateUser(@Valid @PathVariable UUID id, @RequestBody User user) {
        return UserMapper.toDTO(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(	summary = "Delete user by id")
    public ResponseEntity<String> deleteUser(@Valid @PathVariable UUID id){
        userService.deleteUser(id);
        return ResponseEntity.ok("Success");
    }
}
