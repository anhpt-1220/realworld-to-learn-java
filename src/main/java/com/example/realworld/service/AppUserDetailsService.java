package com.example.realworld.service;

import com.example.realworld.entity.UserEntity;
import com.example.realworld.exception.AppException;
import com.example.realworld.exception.Error;
import com.example.realworld.model.AppUserDetails;
import com.example.realworld.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public AppUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        return new AppUserDetails(userEntity);
    }
}
