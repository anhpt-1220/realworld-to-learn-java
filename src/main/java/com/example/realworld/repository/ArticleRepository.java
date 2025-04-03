package com.example.realworld.repository;

import com.example.realworld.entity.ArticleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional<ArticleEntity> findBySlug(@Param("slug") String slug);

    @Query("""
                SELECT DISTINCT article FROM ArticleEntity article
                LEFT JOIN article.tagList tag
                WHERE (:tag IS NULL OR tag.tag = :tag)
                  AND (:author IS NULL OR article.author.username = :author)
            """)
    Page<ArticleEntity> findArticles(
            @Param("tag") String tag,
            @Param("author") String author,
            Pageable pageable);

    @Query("""
                SELECT article FROM ArticleEntity article
                WHERE article.author.id IN :ids
            """)
    Page<ArticleEntity> findByAuthorIds(@Param("ids") List<Long> ids, Pageable pageable);
}
