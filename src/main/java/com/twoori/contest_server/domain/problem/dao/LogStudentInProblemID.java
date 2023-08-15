package com.twoori.contest_server.domain.problem.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class LogStudentInProblemID implements Serializable {
    @Column(name = "contest_id")
    private UUID contestId;

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "content_id")
    private Long contentId;

    public LogStudentInProblemID(UUID contestId, UUID studentId, Long problemId, Long contentId) {
        this.contestId = contestId;
        this.studentId = studentId;
        this.problemId = problemId;
        this.contentId = contentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogStudentInProblemID that = (LogStudentInProblemID) o;
        return Objects.equals(contestId, that.contestId) && Objects.equals(studentId, that.studentId) && Objects.equals(problemId, that.problemId) && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contestId, studentId, problemId, contentId);
    }
}
