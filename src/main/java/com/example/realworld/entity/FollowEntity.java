package com.example.realworld.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "_user_follows", uniqueConstraints = {
        @UniqueConstraint(name = "u_follow_following_pair_must_be_unique", columnNames = {
                "following", "follower"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "following")
    private UserEntity following;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "follower")
    private UserEntity follower;

    public FollowEntity(UserEntity following, UserEntity follower) {
        this.following = following;
        this.follower = follower;
    }
}
