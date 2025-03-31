package com.example.realworld.service;

import com.example.realworld.exception.AppException;
import com.example.realworld.exception.Error;
import com.example.realworld.model.AuthRequest;
import com.example.realworld.model.RegistrationRequest;
import com.example.realworld.model.UserEntity;
import com.example.realworld.model.UserRequest;
import com.example.realworld.model.UserResponse;
import com.example.realworld.repository.UserRepository;
import com.example.realworld.security.TokenUtil;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(RegistrationRequest registrationRequest) {
        Optional.ofNullable(registrationRequest.getEmail())
                .filter(userRepository::existsByEmail)
                .ifPresent(email -> {
                    throw new AppException(Error.EMAIL_TAKEN);
                });
        Optional.ofNullable(registrationRequest.getEmail())
                .filter(userRepository::existsByUsername)
                .ifPresent(username -> {
                    throw new AppException(Error.USERNAME_TAKEN);
                });
        UserEntity userEntity = userRepository.save(new UserEntity(
                registrationRequest.getEmail(),
                registrationRequest.getUsername(),
                passwordEncoder.encode(registrationRequest.getPassword())
        ));
        return UserResponse.builder()
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .image(userEntity.getImage())
                .bio(userEntity.getBio())
                .demo(userEntity.getDemo())
                .token(tokenUtil.generateToken(userEntity.getEmail())).build();
    }

    @Transactional
    public UserResponse loginUser(AuthRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword())
            );
        } catch (Exception e) {
            throw new AppException(Error.INVALID_LOGIN);
        }
        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        return UserResponse.builder()
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .image(userEntity.getImage())
                .bio(userEntity.getBio())
                .demo(userEntity.getDemo())
                .token(tokenUtil.generateToken(userEntity.getEmail())).build();
    }

    @Transactional
    public UserResponse getCurrentUser() {
        UserEntity userEntity = getCurrentUserEntity();
        return UserResponse.builder()
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .image(userEntity.getImage())
                .bio(userEntity.getBio())
                .demo(userEntity.getDemo())
                .token(tokenUtil.generateToken(userEntity.getEmail())).build();
    }

    @Transactional
    public UserResponse updateCurrentUser(UserRequest userRequest) {
        Optional.ofNullable(userRequest.getEmail())
                .filter(userRepository::existsByEmail)
                .ifPresent(email -> {
                    throw new AppException(Error.EMAIL_TAKEN);
                });
        Optional.ofNullable(userRequest.getUsername())
                .filter(userRepository::existsByUsername)
                .ifPresent(username -> {
                    throw new AppException(Error.USERNAME_TAKEN);
                });
        UserEntity currentUser = updateUserEntity(userRequest);
        UserEntity userEntity = userRepository.save(currentUser);
        return UserResponse.builder()
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .image(userEntity.getImage())
                .bio(userEntity.getBio())
                .demo(userEntity.getDemo())
                .token(tokenUtil.generateToken(userEntity.getEmail())).build();
    }

    private UserEntity updateUserEntity(UserRequest userRequest) {
        UserEntity currentUser = getCurrentUserEntity();
        Optional.ofNullable(userRequest.getEmail()).filter(email -> !email.isEmpty())
                .ifPresent(currentUser::setEmail);
        Optional.ofNullable(userRequest.getUsername()).filter(username -> !username.isEmpty())
                .ifPresent(currentUser::setUsername);
        Optional.ofNullable(userRequest.getBio()).filter(bio -> !bio.isEmpty())
                .ifPresent(currentUser::setBio);
        Optional.ofNullable(userRequest.getDemo()).ifPresent(currentUser::setDemo);
        Optional.ofNullable(userRequest.getImage()).filter(image -> !image.isEmpty())
                .ifPresent(currentUser::setImage);
        return currentUser;
    }

    private UserEntity getCurrentUserEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
    }
}
