package com.example.realworld.service;

import com.example.realworld.dto.CommentReqDto;
import com.example.realworld.dto.CommentResDto;
import com.example.realworld.dto.ProfileResDto;
import com.example.realworld.entity.ArticleEntity;
import com.example.realworld.entity.CommentEntity;
import com.example.realworld.entity.UserEntity;
import com.example.realworld.exception.AppException;
import com.example.realworld.exception.Error;
import com.example.realworld.repository.ArticleRepository;
import com.example.realworld.repository.CommentRepository;
import com.example.realworld.repository.FollowRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserService userService;

    public CommentResDto.SingleComment createCommentsToAnArticle(String slug,
            CommentReqDto commentReqDto) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        UserEntity currentUser = userService.getCurrentUserEntity();
        CommentEntity commentEntity = commentRepository.save(
                new CommentEntity(commentReqDto.getBody(), currentUser, articleEntity));
        return new CommentResDto.SingleComment(
                CommentResDto.builder()
                        .id(commentEntity.getId())
                        .body(commentEntity.getBody())
                        .createdAt(commentEntity.getCreatedAt())
                        .updatedAt(commentEntity.getUpdatedAt())
                        .author(ProfileResDto.builder()
                                .username(currentUser.getUsername())
                                .bio(currentUser.getBio())
                                .image(currentUser.getImage())
                                .following(false).build())
                        .build()
        );
    }

    public CommentResDto.MultipleComments getCommentsToAnArticle(String slug) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        List<CommentEntity> commentEntities = commentRepository.findByArticleId(
                articleEntity.getId());
        final List<Long> followingIds = new ArrayList<>();
        try {
            UserEntity currentUser = userService.getCurrentUserEntity();
            followingIds.addAll(followRepository.findByFollowedById(currentUser.getId()).stream()
                    .map(e -> e.getFollowing().getId()).toList());
        } catch (AppException ignored) {
        }
        return new CommentResDto.MultipleComments(
                commentEntities.stream().map(commentEntity -> CommentResDto.builder()
                        .id(commentEntity.getId())
                        .body(commentEntity.getBody())
                        .createdAt(commentEntity.getCreatedAt())
                        .updatedAt(commentEntity.getUpdatedAt())
                        .author(ProfileResDto.builder()
                                .username(articleEntity.getAuthor().getUsername())
                                .bio(articleEntity.getAuthor().getBio())
                                .image(articleEntity.getAuthor().getImage())
                                .following(followingIds.contains(articleEntity.getId())).build())
                        .build()).toList()
        );
    }

    public void deleteComment(String slug, Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .filter(e -> e.getArticle().getSlug().equals(slug))
                .orElseThrow(() -> new AppException(Error.COMMENT_NOT_FOUND));
        commentRepository.delete(commentEntity);
    }
}
