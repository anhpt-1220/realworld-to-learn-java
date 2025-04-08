package com.example.realworld.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleResDto {

    private String title;

    private String slug;

    private String description;

    private String body;

    private List<String> tagList;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private int favoritesCount = 0;

    @Builder.Default
    private boolean favorited = false;

    private ProfileResDto author;

    @Data
    @AllArgsConstructor
    public static class MultiArticlesResDto {

        List<ArticleResDto> articles;

        long articlesCount;
    }

    @Data
    @AllArgsConstructor
    public static class SingleArticlesResDto {

        ArticleResDto article;
    }
}
