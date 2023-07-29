package com.twoori.contest_server.domain.problem.dao;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@SQLDelete(sql = "UPDATE sequence SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "sequence")
public class Sequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private String question;
    private String answer;

}