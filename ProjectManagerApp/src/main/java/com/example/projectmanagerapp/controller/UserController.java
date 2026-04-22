package com.example.projectmanagerapp.controller;

import com.example.projectmanagerapp.dto.CreateUserRequest;
import com.example.projectmanagerapp.model.User;
import com.example.projectmanagerapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all users", description = "Returns a list of all users from the database")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Adds a new user to the database")
    public ResponseEntity<User> createUser(
            @Parameter(description = "Data required to create a new user")
            @RequestBody CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        User saved = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}