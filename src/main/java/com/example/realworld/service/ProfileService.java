package com.example.realworld.service;

import com.example.realworld.dto.ProfileResDto;
import com.example.realworld.entity.FollowEntity;
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
        boolean following = followRepository.findByFollowingIdAndFollowedById(user.getId(),
                currentUserEntity.getId()).isPresent();
        return ProfileResDto.builder()
                .username(user.getUsername())
                .image(user.getImage())
                .bio(user.getBio())
                .following(following)
                .build();
    }

    public ProfileResDto followUser(String name) {
        UserEntity followingUser = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(
                        Error.USER_NOT_FOUND));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity followedBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        Error.USER_NOT_FOUND
                ));
        followRepository.findByFollowingIdAndFollowedById(followingUser.getId(), followedBy.getId())
                .ifPresent(follow -> {
                    throw new AppException(Error.ALREADY_FOLLOWED_USER);
                });

        followRepository.save(new FollowEntity(followingUser, followedBy));
        return ProfileResDto.builder()
                .username(followingUser.getUsername())
                .image(followingUser.getImage())
                .bio(followingUser.getBio())
                .following(true)
                .build();
    }

    public ProfileResDto unfollowUser(String name) {
        UserEntity followingUser = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(
                        Error.USER_NOT_FOUND));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity followerUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        Error.USER_NOT_FOUND
                ));
        FollowEntity follow = followRepository.findByFollowingIdAndFollowedById(followingUser.getId(),
                        followerUser.getId())
                .orElseThrow(() -> new AppException(Error.FOLLOW_NOT_FOUND));
        followRepository.delete(follow);
        return ProfileResDto.builder()
                .username(followingUser.getUsername())
                .image(followingUser.getImage())
                .bio(followingUser.getBio())
                .following(false)
                .build();
    }
}
