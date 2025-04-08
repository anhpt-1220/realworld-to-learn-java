package com.example.realworld.repository;

import com.example.realworld.entity.FollowEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFollowingIdAndFollowerId(Long followingId, Long followerId);
}
