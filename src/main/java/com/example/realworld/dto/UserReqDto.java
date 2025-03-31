package com.example.realworld.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeName("user")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserReqDto {

    private String email;

    private String username;

    private String image;

    private String bio;

    private Boolean demo;
}
