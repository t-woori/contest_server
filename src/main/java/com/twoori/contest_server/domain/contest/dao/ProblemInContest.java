package com.twoori.contest_server.domain.contest.dao;

import com.twoori.contest_server.domain.problem.dao.Problem;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "problem_in_contest")
public class ProblemInContest {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private Integer noOfProblemInContest;

}