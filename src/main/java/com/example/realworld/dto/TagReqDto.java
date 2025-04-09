package com.example.realworld.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TagReqDto {

    List<String> tags;
}

