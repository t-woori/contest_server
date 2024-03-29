package com.twoori.contest_server.domain.problem.dao;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.student.dao.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "log_student_in_problem")
public class LogStudentInProblem {

    @EmbeddedId
    private LogStudentInProblemID logStudentInProblemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    @MapsId("contest_id")
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @MapsId("student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    @JoinColumn(name = "problem_id")
    @MapsId("content_id")
    private Content content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private Double score;


    public LogStudentInProblem(LogStudentInProblemID logStudentInProblemId, Double score) {
        this.logStudentInProblemId = logStudentInProblemId;
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogStudentInProblem that = (LogStudentInProblem) o;
        return Objects.equals(logStudentInProblemId, that.logStudentInProblemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logStudentInProblemId);
    }
}