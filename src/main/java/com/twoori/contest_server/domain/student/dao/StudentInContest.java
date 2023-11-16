package com.twoori.contest_server.domain.student.dao;

import com.twoori.contest_server.domain.contest.dao.Contest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE student_in_contest SET deleted_at = NOW() WHERE student_id =? and contest_id=?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "student_in_contest")
public class StudentInContest {

    @EmbeddedId
    private StudentInContestID id;

    @MapsId("contest_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Contest contest;

    @MapsId("student_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isResigned;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isEntered;

    @Setter
    private LocalDateTime endContestAt;

    @Setter
    private Double studentScore;

    @Setter
    private Long studentRank;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentInContest that = (StudentInContest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
