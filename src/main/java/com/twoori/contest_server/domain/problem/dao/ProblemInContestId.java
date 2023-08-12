package com.twoori.contest_server.domain.problem.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class ProblemInContestId implements Serializable {
    @Column(name = "problem_id")
    private Long problemId;
    @Column(name = "contest_id")
    private UUID contestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemInContestId that = (ProblemInContestId) o;
        return Objects.equals(problemId, that.problemId) && Objects.equals(contestId, that.contestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId, contestId);
    }
}
