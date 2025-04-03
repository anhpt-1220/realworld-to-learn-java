package com.example.realworld.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeName("article")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateArticleReqDto {

    private String title;

    private String description;

    private String body;
}
