package com.twoori.contest_server.domain.problem.repository;

public interface ProblemRepositoryCustom {
    ProblemDto getProblem(ProblemCondition condition);
}
