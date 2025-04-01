package com.example.realworld.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum Error {
    EMAIL_TAKEN("Email has already been taken", HttpStatus.UNPROCESSABLE_ENTITY),
    USERNAME_TAKEN("Username has already been taken", HttpStatus.UNPROCESSABLE_ENTITY),
    USER_NOT_FOUND("User not found", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_LOGIN("Invalid email or password", HttpStatus.UNAUTHORIZED),
    ALREADY_FOLLOWED_USER("Already followed user", HttpStatus.UNPROCESSABLE_ENTITY),
    FOLLOW_NOT_FOUND("Follow not found", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String message;
    private final HttpStatus status;

    Error(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
