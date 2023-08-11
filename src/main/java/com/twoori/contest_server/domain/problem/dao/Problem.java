package com.twoori.contest_server.domain.problem.dao;

import com.twoori.contest_server.domain.problem.dao.enums.GRADE;
import com.twoori.contest_server.domain.problem.dao.enums.PROBLEM_TYPE;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE problem SET deleted_at = NOW() WHERE problem_id = ? AND sequence_id = ?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "problem")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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
    private PROBLEM_TYPE type;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "problem")
    private List<Content> contents;

}