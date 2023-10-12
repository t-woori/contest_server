package com.twoori.contest_server.domain.contest.dao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE contest SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "contest")
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private String authCode;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String hostName;

    @Column(nullable = false)
    private LocalDateTime runningStartDateTime;

    @Column(nullable = false)
    private LocalDateTime runningEndDateTime;

    @Column(nullable = false)
    private Double rateAccuracyByScoring;

    @Column(nullable = false)
    private Double rateTimeByScoring;

    public Contest(UUID id, String authCode, String name, String hostName, LocalDateTime runningStartDateTime,
                   LocalDateTime runningEndDateTime, Double rateAccuracyByScoring, Double rateTimeByScoring) {
        this.id = id;
        this.authCode = authCode;
        this.name = name;
        this.hostName = hostName;
        this.runningStartDateTime = runningStartDateTime;
        this.runningEndDateTime = runningEndDateTime;
        this.rateAccuracyByScoring = rateAccuracyByScoring;
        this.rateTimeByScoring = rateTimeByScoring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contest contest = (Contest) o;
        return Objects.equals(id, contest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}