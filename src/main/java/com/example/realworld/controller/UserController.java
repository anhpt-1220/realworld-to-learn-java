package com.example.realworld.controller;

import com.example.realworld.model.AuthRequest;
import com.example.realworld.model.RegistrationRequest;
import com.example.realworld.model.UserRequest;
import com.example.realworld.model.UserResponse;
import com.example.realworld.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest) {
        UserResponse userResponse = userService.registerUser(registrationRequest);
        return ResponseEntity.status(201).body(userResponse);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponse> loginUser(@Valid @RequestBody AuthRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/user")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateCurrentUser(userRequest));
    }
}
