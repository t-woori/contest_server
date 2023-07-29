package com.twoori.contest_server.domain.problem.dao;

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
@SQLDelete(sql = "UPDATE problem SET deleted_at = NOW() WHERE id = ?")
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

    @Column(nullable = false)
    private String title;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "chapter_name")
    private String chapterName;
    @Column(nullable = false)
    private Integer grade;
    @Column(nullable = false)
    private Integer type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "problem")
    private List<Sequence> sequences;

}