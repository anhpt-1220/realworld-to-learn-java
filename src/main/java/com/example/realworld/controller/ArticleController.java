package com.example.realworld.controller;

import com.example.realworld.dto.ArticleReqDto;
import com.example.realworld.dto.ArticleResDto;
import com.example.realworld.dto.ArticleResDto.MultiArticlesResDto;
import com.example.realworld.dto.ArticleResDto.SingleArticlesResDto;
import com.example.realworld.dto.UpdateArticleReqDto;
import com.example.realworld.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseEntity<SingleArticlesResDto> createArticle(
            @Valid @RequestBody ArticleReqDto articleReqDto) {
        return ResponseEntity.ok(articleService.createArticle(articleReqDto));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<SingleArticlesResDto> getArticle(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getArticle(slug));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<SingleArticlesResDto> getArticle(
            @PathVariable String slug,
            @Valid @RequestBody UpdateArticleReqDto updateArticleReqDto) {
        return ResponseEntity.ok(articleService.updateArticle(slug, updateArticleReqDto));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<ArticleResDto> deleteArticle(@PathVariable String slug) {
        articleService.deleteArticle(slug);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<MultiArticlesResDto> getArticlesByTag(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(articleService.getArticlesByTag(tag, author, offset, limit));
    }

    @GetMapping("/feed")
    public ResponseEntity<MultiArticlesResDto> getFeedArticles(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(articleService.getFeedArticles(offset, limit));
    }
}
