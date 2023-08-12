package com.twoori.contest_server.domain.problem.dao;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE content SET deleted_at = NOW() WHERE problem_id = ? AND content_id = ?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "content")
public class Content {

    @EmbeddedId
    private ContentCompositeID contentCompositeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "problem_id")
    @MapsId("problem_id")
    private Problem problem;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private String preScript;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    private String postScript;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(contentCompositeId, content.contentCompositeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentCompositeId);
    }
}