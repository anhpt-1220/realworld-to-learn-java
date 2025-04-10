package com.example.realworld.controller;

import com.example.realworld.dto.TagReqDto;
import com.example.realworld.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<TagReqDto> listOfTags() {
        return ResponseEntity.ok(tagService.getTags());
    }
}
