package com.twoori.contest_server.domain.problem.dao;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE content SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "content")
public class Content {

    @EmbeddedId
    private ContentID contentID;

    @MapsId("problem_id")
    @ManyToOne(optional = false)
    private Problem problem;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private String preScript;
    @Column(nullable = false, unique = true)
    private String question;
    @Column(nullable = false)
    private String answer;
    private String postScript;

}