package com.example.realworld.controller;

import com.example.realworld.dto.AuthReqDto;
import com.example.realworld.dto.RegistrationReqDto;
import com.example.realworld.dto.UserReqDto;
import com.example.realworld.dto.UserResDto;
import com.example.realworld.model.AppUserDetails;
import com.example.realworld.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<UserResDto> registerUser(
            @Valid @RequestBody RegistrationReqDto registrationReqDto) {
        UserResDto userResponse = userService.registerUser(registrationReqDto);
        return ResponseEntity.status(201).body(userResponse);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResDto> loginUser(@Valid @RequestBody AuthReqDto loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<UserResDto> getCurrentUser(
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(appUserDetails));
    }

    @PutMapping("/user")
    public ResponseEntity<UserResDto> updateCurrentUser(
            @Valid @RequestBody UserReqDto userReqDto,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(userService.updateCurrentUser(userReqDto, appUserDetails));
    }
}
