package com.twoori.contest_server.domain.problem.dao;

import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE problem SET deleted_at = NOW() WHERE problem_id = ? AND sequence_id = ?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "problem")
public class Problem {
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(name = "image_url")
    private String imageURL;

    @Column(nullable = false)
    private GRADE grade;
    @Column(nullable = false)
    private PROBLEM_TYPE problemType;
    @Column(nullable = false)
    private CHAPTER_TYPE chapterType;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "problem")
    private List<Content> contents;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return Objects.equals(id, problem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}