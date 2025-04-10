package com.example.realworld.service;

import com.example.realworld.dto.TagReqDto;
import com.example.realworld.entity.TagEntity;
import com.example.realworld.repository.TagRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public TagReqDto getTags() {
        List<String> tags = tagRepository.findAll().stream().map(TagEntity::getTag).distinct()
                .toList();
        return new TagReqDto(tags);
    }
}
