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

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "no_of_problem_in_contest")
    private Long noOfProblemInContest;

    @Column(name = "contest_id")
    private UUID contestId;

    public LogStudentInProblemID(UUID contestId, UUID studentId, Long noOfProblemInContest) {
        this.contestId = contestId;
        this.studentId = studentId;
        this.noOfProblemInContest = noOfProblemInContest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogStudentInProblemID that = (LogStudentInProblemID) o;
        return Objects.equals(studentId, that.studentId) && Objects.equals(noOfProblemInContest, that.noOfProblemInContest) && Objects.equals(contestId, that.contestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, noOfProblemInContest, contestId);
    }
}
