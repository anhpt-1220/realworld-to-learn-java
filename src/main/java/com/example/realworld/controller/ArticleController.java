package com.example.realworld.controller;

import com.example.realworld.dto.ArticleReqDto;
import com.example.realworld.dto.ArticleResDto;
import com.example.realworld.dto.ArticleResDto.MultiArticlesResDto;
import com.example.realworld.dto.ArticleResDto.SingleArticlesResDto;
import com.example.realworld.dto.CommentReqDto;
import com.example.realworld.dto.CommentResDto;
import com.example.realworld.dto.UpdateArticleReqDto;
import com.example.realworld.model.AppUserDetails;
import com.example.realworld.service.ArticleService;
import com.example.realworld.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<SingleArticlesResDto> createArticle(
            @Valid @RequestBody ArticleReqDto articleReqDto,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(articleService.createArticle(articleReqDto, appUserDetails));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<SingleArticlesResDto> getArticle(@PathVariable String slug,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(articleService.getArticle(slug, appUserDetails));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<SingleArticlesResDto> getArticle(
            @PathVariable String slug,
            @Valid @RequestBody UpdateArticleReqDto updateArticleReqDto,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(
                articleService.updateArticle(slug, updateArticleReqDto, appUserDetails));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<ArticleResDto> deleteArticle(@PathVariable String slug,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        articleService.deleteArticle(slug, appUserDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<MultiArticlesResDto> getArticlesByTag(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(
                articleService.getArticlesByTag(tag, author, offset, limit, appUserDetails));
    }

    @GetMapping("/feed")
    public ResponseEntity<MultiArticlesResDto> getFeedArticles(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(articleService.getFeedArticles(offset, limit, appUserDetails));
    }

    @PostMapping("/{slug}/comments")
    public ResponseEntity<CommentResDto.SingleComment> createCommentsToAnArticle(
            @PathVariable String slug, @RequestBody @Valid CommentReqDto commentReqDto,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(
                commentService.createCommentsToAnArticle(slug, commentReqDto, appUserDetails));
    }

    @GetMapping("/{slug}/comments")
    public ResponseEntity<CommentResDto.MultipleComments> getCommentsToAnArticle(
            @PathVariable String slug, @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(commentService.getCommentsToAnArticle(slug, appUserDetails));
    }


    @DeleteMapping("/{slug}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("slug") String slug,
            @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(slug, commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{slug}/favorite")
    public ResponseEntity<ArticleResDto.SingleArticlesResDto> favoriteArticle(
            @PathVariable String slug, @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(articleService.favoriteArticle(slug, appUserDetails));
    }

    @DeleteMapping("/{slug}/favorite")
    public ResponseEntity<ArticleResDto.SingleArticlesResDto> unfavoriteArticle(
            @PathVariable String slug, @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(articleService.unfavoriteArticle(slug, appUserDetails));
    }
}
