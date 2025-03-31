package com.example.realworld.controller;

import com.example.realworld.model.ProfileResponse;
import com.example.realworld.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getProfile(username));
    }
}