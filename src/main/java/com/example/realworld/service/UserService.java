package com.example.realworld.service;

import com.example.realworld.dto.AuthReqDto;
import com.example.realworld.dto.RegistrationReqDto;
import com.example.realworld.dto.UserReqDto;
import com.example.realworld.entity.UserEntity;
import com.example.realworld.exception.AppException;
import com.example.realworld.exception.Error;
import com.example.realworld.model.UserResponse;
import com.example.realworld.repository.UserRepository;
import com.example.realworld.security.TokenUtil;
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

    public UserResponse registerUser(RegistrationReqDto registrationReqDto) {
        Optional.ofNullable(registrationReqDto.getEmail())
                .filter(userRepository::existsByEmail)
                .ifPresent(email -> {
                    throw new AppException(Error.EMAIL_TAKEN);
                });
        Optional.ofNullable(registrationReqDto.getEmail())
                .filter(userRepository::existsByUsername)
                .ifPresent(username -> {
                    throw new AppException(Error.USERNAME_TAKEN);
                });
        UserEntity userEntity = userRepository.save(new UserEntity(
                registrationReqDto.getEmail(),
                registrationReqDto.getUsername(),
                passwordEncoder.encode(registrationReqDto.getPassword())
        ));
        return UserResponse.builder()
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .image(userEntity.getImage())
                .bio(userEntity.getBio())
                .demo(userEntity.getDemo())
                .token(tokenUtil.generateToken(userEntity.getEmail())).build();
    }

    public UserResponse loginUser(AuthReqDto loginRequest) {
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

    public UserResponse updateCurrentUser(UserReqDto userReqDto) {
        Optional.ofNullable(userReqDto.getEmail())
                .filter(userRepository::existsByEmail)
                .ifPresent(email -> {
                    throw new AppException(Error.EMAIL_TAKEN);
                });
        Optional.ofNullable(userReqDto.getUsername())
                .filter(userRepository::existsByUsername)
                .ifPresent(username -> {
                    throw new AppException(Error.USERNAME_TAKEN);
                });
        UserEntity currentUser = updateUserEntity(userReqDto);
        UserEntity userEntity = userRepository.save(currentUser);
        return UserResponse.builder()
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .image(userEntity.getImage())
                .bio(userEntity.getBio())
                .demo(userEntity.getDemo())
                .token(tokenUtil.generateToken(userEntity.getEmail())).build();
    }

    private UserEntity updateUserEntity(UserReqDto userReqDto) {
        UserEntity currentUser = getCurrentUserEntity();
        Optional.ofNullable(userReqDto.getEmail()).filter(email -> !email.isEmpty())
                .ifPresent(currentUser::setEmail);
        Optional.ofNullable(userReqDto.getUsername()).filter(username -> !username.isEmpty())
                .ifPresent(currentUser::setUsername);
        Optional.ofNullable(userReqDto.getBio()).filter(bio -> !bio.isEmpty())
                .ifPresent(currentUser::setBio);
        Optional.ofNullable(userReqDto.getDemo()).ifPresent(currentUser::setDemo);
        Optional.ofNullable(userReqDto.getImage()).filter(image -> !image.isEmpty())
                .ifPresent(currentUser::setImage);
        return currentUser;
    }

    private UserEntity getCurrentUserEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
    }
}
