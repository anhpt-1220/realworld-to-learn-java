package com.example.realworld.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;

@JsonTypeName("profile")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@Data
@Builder
public class ProfileResponse {

    private String username;

    private String image;

    private String bio;

    private Boolean following;
}
