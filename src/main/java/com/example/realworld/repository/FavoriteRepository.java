package com.example.realworld.repository;

import com.example.realworld.entity.FavoriteEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {

    Optional<FavoriteEntity> findByArticleIdAndUserId(Long articleId, Long userId);
}
