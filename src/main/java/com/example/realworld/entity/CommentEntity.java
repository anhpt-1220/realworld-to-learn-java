package com.example.realworld.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime updatedAt;

    public CommentEntity(String body, UserEntity author, ArticleEntity article) {
        this.body = body;
        this.author = author;
        this.article = article;
    }
}