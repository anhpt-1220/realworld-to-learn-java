package com.example.realworld.repository;

import com.example.realworld.model.FollowEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFollowingIdAndFollowerId(Long followingId, Long followerId);
}
