package com.example.realworld.service;

import com.example.realworld.dto.ProfileResDto;
import com.example.realworld.entity.UserEntity;
import com.example.realworld.exception.AppException;
import com.example.realworld.exception.Error;
import com.example.realworld.repository.FollowRepository;
import com.example.realworld.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;


    public ProfileResDto getProfile(String name) {
        UserEntity user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(
                Error.USER_NOT_FOUND));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUserEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        Error.USER_NOT_FOUND
                ));
        boolean following = followRepository.findByFollowingIdAndFollowerId(user.getId(),
                currentUserEntity.getId()).isPresent();
        return ProfileResDto.builder()
                .username(user.getUsername())
                .image(user.getImage())
                .bio(user.getBio())
                .following(following)
                .build();
    }
}
