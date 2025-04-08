package com.example.realworld.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentResDto {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String body;

    private ProfileResDto author;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class SingleComment {
        CommentResDto comment;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class MultipleComments {
        List<CommentResDto> comments;
    }
}
