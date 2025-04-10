package com.example.realworld.service;

import com.example.realworld.dto.ArticleReqDto;
import com.example.realworld.dto.ArticleResDto;
import com.example.realworld.dto.ArticleResDto.MultiArticlesResDto;
import com.example.realworld.dto.ArticleResDto.SingleArticlesResDto;
import com.example.realworld.dto.ProfileResDto;
import com.example.realworld.dto.UpdateArticleReqDto;
import com.example.realworld.entity.ArticleEntity;
import com.example.realworld.entity.FavoriteEntity;
import com.example.realworld.entity.TagEntity;
import com.example.realworld.entity.UserEntity;
import com.example.realworld.exception.AppException;
import com.example.realworld.exception.Error;
import com.example.realworld.model.AppUserDetails;
import com.example.realworld.repository.ArticleRepository;
import com.example.realworld.repository.FavoriteRepository;
import com.example.realworld.repository.FollowRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public SingleArticlesResDto createArticle(ArticleReqDto articleReqDto,
            AppUserDetails appUserDetails) {
        String slug =
                String.join("-", articleReqDto.getTitle().split(" ")) + "-" + UUID.randomUUID()
                        .toString().substring(0, 8);
        UserEntity currentUser = appUserDetails.getUserEntity();
        ArticleEntity articleEntity = new ArticleEntity(slug, articleReqDto.getTitle(),
                articleReqDto.getDescription(), articleReqDto.getBody(), currentUser);
        articleEntity.setTagList(
                articleReqDto.getTagList().stream().map(tag -> new TagEntity(tag, articleEntity))
                        .toList());
        ArticleEntity savedArticleEntity = articleRepository.save(articleEntity);
        ProfileResDto profileResDto = ProfileResDto.builder()
                .username(currentUser.getUsername())
                .bio(currentUser.getBio())
                .image(currentUser.getImage()).build();
        return new SingleArticlesResDto(ArticleResDto.builder()
                .title(savedArticleEntity.getTitle())
                .slug(savedArticleEntity.getSlug())
                .description(savedArticleEntity.getDescription())
                .body(savedArticleEntity.getBody())
                .createdAt(savedArticleEntity.getCreatedAt())
                .updatedAt(savedArticleEntity.getUpdatedAt())
                .favorited(savedArticleEntity.getFavoriteList().stream()
                        .anyMatch(f -> f.getId().equals(savedArticleEntity.getAuthor().getId())))
                .author(profileResDto)
                .tagList(savedArticleEntity.getTagList().stream().map(TagEntity::getTag).toList())
                .build()
        );
    }

    public SingleArticlesResDto getArticle(String slug, AppUserDetails appUserDetails) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        UserEntity currentUser = appUserDetails.getUserEntity();
        boolean following = followRepository.findByFollowingIdAndFollowedById(articleEntity.getId(),
                currentUser.getId()).isPresent();
        ProfileResDto profileResDto = (ProfileResDto.builder()
                .username(articleEntity.getAuthor().getUsername())
                .bio(articleEntity.getAuthor().getBio()).image(articleEntity.getAuthor().getImage())
                .following(following).build());
        return new SingleArticlesResDto(ArticleResDto.builder()
                .title(articleEntity.getTitle())
                .slug(articleEntity.getSlug())
                .description(articleEntity.getDescription())
                .body(articleEntity.getBody())
                .createdAt(articleEntity.getCreatedAt())
                .updatedAt(articleEntity.getUpdatedAt())
                .favorited(articleEntity.getFavoriteList().stream()
                        .anyMatch(f -> f.getId().equals(articleEntity.getAuthor().getId())))
                .author(profileResDto)
                .tagList(articleEntity.getTagList().stream().map(TagEntity::getTag).toList())
                .build()
        );
    }

    @Transactional
    public SingleArticlesResDto updateArticle(String slug,
            UpdateArticleReqDto updateArticleReqDto, AppUserDetails appUserDetails) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        Optional.ofNullable(updateArticleReqDto.getTitle()).filter(title -> !title.isEmpty())
                .ifPresent(title -> {
                    String newSlug =
                            String.join("-", updateArticleReqDto.getTitle().split(" ")) + "-"
                                    + UUID.randomUUID().toString().substring(0, 8);
                    articleEntity.setSlug(newSlug);
                    articleEntity.setTitle(title);
                });
        Optional.ofNullable(updateArticleReqDto.getDescription())
                .filter(description -> !description.isEmpty())
                .ifPresent(articleEntity::setDescription);
        Optional.ofNullable(updateArticleReqDto.getBody()).filter(body -> !body.isEmpty())
                .ifPresent(articleEntity::setBody);
        ArticleEntity savedArticleEntity = articleRepository.save(articleEntity);
        UserEntity currentUser = appUserDetails.getUserEntity();
        boolean following = followRepository.findByFollowingIdAndFollowedById(
                articleEntity.getAuthor().getId(), currentUser.getId()).isPresent();
        ProfileResDto profileResDto = ProfileResDto.builder()
                .username(savedArticleEntity.getAuthor().getUsername())
                .bio(savedArticleEntity.getAuthor().getBio())
                .image(savedArticleEntity.getAuthor().getImage())
                .following(following).build();
        return new SingleArticlesResDto(
                ArticleResDto.builder()
                        .title(savedArticleEntity.getTitle())
                        .slug(savedArticleEntity.getSlug())
                        .description(savedArticleEntity.getDescription())
                        .body(savedArticleEntity.getBody())
                        .createdAt(savedArticleEntity.getCreatedAt())
                        .updatedAt(savedArticleEntity.getUpdatedAt())
                        .author(profileResDto)
                        .favorited(articleEntity.getFavoriteList().stream()
                                .anyMatch(f -> f.getId().equals(articleEntity.getAuthor().getId())))
                        .tagList(savedArticleEntity.getTagList().stream().map(TagEntity::getTag)
                                .toList())
                        .build()
        );
    }

    @Transactional
    public void deleteArticle(String slug, AppUserDetails appUserDetails) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        UserEntity currentUser = appUserDetails.getUserEntity();
        if (!articleEntity.getAuthor().getId().equals(currentUser.getId())) {
            throw new AppException(Error.ARTICLE_DELETE_FORBIDDEN);
        }
        articleRepository.delete(articleEntity);
    }

    public MultiArticlesResDto getArticlesByTag(String tag, String author, int offset, int limit,
            @Nullable AppUserDetails appUserDetails) {
        Pageable pageable = PageRequest.of(offset, limit);
        final UserEntity currentUser =
                appUserDetails != null ? appUserDetails.getUserEntity() : null;
        final List<Long> followingIds = new ArrayList<>();
        if (currentUser != null) {
            followingIds.addAll(followRepository.findByFollowedById(currentUser.getId())
                    .stream()
                    .map(e -> e.getFollowing().getId()).toList());
        }

        Page<ArticleEntity> articleEntities = articleRepository.findArticles(tag, author, pageable);
        return new MultiArticlesResDto(articleEntities.stream()
                .map(e -> ArticleResDto.builder()
                        .title(e.getTitle())
                        .slug(e.getSlug())
                        .description(e.getDescription())
                        .body(e.getBody())
                        .tagList(e.getTagList().stream().map(TagEntity::getTag).toList())
                        .createdAt(e.getCreatedAt())
                        .updatedAt(e.getUpdatedAt())
                        .favorited(currentUser != null && e.getFavoriteList().stream().anyMatch(
                                favoriteEntity -> favoriteEntity.getUser().getId()
                                        .equals(currentUser.getId())))
                        .author(ProfileResDto.builder()
                                .username(e.getAuthor().getUsername())
                                .image(e.getAuthor().getImage())
                                .bio(e.getAuthor().getBio())
                                .following(followingIds.contains(e.getAuthor().getId())).build())
                        .build()
                ).toList(),
                articleEntities.getTotalElements());
    }

    public MultiArticlesResDto getFeedArticles(int offset, int limit,
            AppUserDetails appUserDetails) {
        Pageable pageable = PageRequest.of(offset, limit);
        UserEntity currentUser = appUserDetails.getUserEntity();
        List<Long> followingIds = followRepository.findByFollowedById(currentUser.getId()).stream()
                .map(e -> e.getFollowing().getId()).toList();
        Page<ArticleEntity> articleEntities = articleRepository.findByAuthorIds(followingIds,
                pageable);
        return new MultiArticlesResDto(articleEntities.stream()
                .map(e -> ArticleResDto.builder()
                        .title(e.getTitle())
                        .slug(e.getSlug())
                        .description(e.getDescription())
                        .body(e.getBody())
                        .tagList(e.getTagList().stream().map(TagEntity::getTag).toList())
                        .createdAt(e.getCreatedAt())
                        .updatedAt(e.getUpdatedAt())
                        .favorited(e.getFavoriteList().stream()
                                .anyMatch(f -> f.getId().equals(e.getAuthor().getId())))
                        .author(ProfileResDto.builder()
                                .username(e.getAuthor().getUsername())
                                .image(e.getAuthor().getImage())
                                .bio(e.getAuthor().getBio())
                                .following(followingIds.contains(e.getAuthor().getId())).build())
                        .build()
                ).toList(),
                articleEntities.getTotalElements());
    }

    public SingleArticlesResDto favoriteArticle(String slug, AppUserDetails appUserDetails) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        UserEntity currentUser = appUserDetails.getUserEntity();
        favoriteRepository.findByArticleIdAndUserId(articleEntity.getId(), currentUser.getId())
                .ifPresent(favoriteEntity -> {
                    throw new AppException(Error.ALREADY_FAVORITE_ARTICLE);
                });
        boolean following = followRepository.findByFollowingIdAndFollowedById(
                articleEntity.getAuthor().getId(), currentUser.getId()).isPresent();
        favoriteRepository.save(new FavoriteEntity(articleEntity, currentUser));
        ProfileResDto profileResDto = ProfileResDto.builder()
                .username(articleEntity.getAuthor().getUsername())
                .bio(articleEntity.getAuthor().getBio())
                .image(articleEntity.getAuthor().getImage())
                .following(following).build();
        return new SingleArticlesResDto(
                ArticleResDto.builder()
                        .title(articleEntity.getTitle())
                        .slug(articleEntity.getSlug())
                        .description(articleEntity.getDescription())
                        .body(articleEntity.getBody())
                        .createdAt(articleEntity.getCreatedAt())
                        .updatedAt(articleEntity.getUpdatedAt())
                        .author(profileResDto)
                        .favorited(true)
                        .tagList(articleEntity.getTagList().stream().map(TagEntity::getTag)
                                .toList())
                        .build()
        );
    }

    public SingleArticlesResDto unfavoriteArticle(String slug, AppUserDetails appUserDetails) {
        ArticleEntity articleEntity = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        UserEntity currentUser = appUserDetails.getUserEntity();
        FavoriteEntity favoriteEntity = favoriteRepository.findByArticleIdAndUserId(
                        articleEntity.getId(), currentUser.getId())
                .orElseThrow(() -> new AppException(Error.FAVORITE_NOT_FOUND));
        boolean following = followRepository.findByFollowingIdAndFollowedById(
                articleEntity.getAuthor().getId(), currentUser.getId()).isPresent();
        favoriteRepository.delete(favoriteEntity);
        ProfileResDto profileResDto = ProfileResDto.builder()
                .username(articleEntity.getAuthor().getUsername())
                .bio(articleEntity.getAuthor().getBio())
                .image(articleEntity.getAuthor().getImage())
                .following(following).build();
        return new SingleArticlesResDto(
                ArticleResDto.builder()
                        .title(articleEntity.getTitle())
                        .slug(articleEntity.getSlug())
                        .description(articleEntity.getDescription())
                        .body(articleEntity.getBody())
                        .createdAt(articleEntity.getCreatedAt())
                        .updatedAt(articleEntity.getUpdatedAt())
                        .author(profileResDto)
                        .favorited(false)
                        .tagList(articleEntity.getTagList().stream().map(TagEntity::getTag)
                                .toList())
                        .build()
        );
    }
}
