package com.twoori.contest_server.domain.problem.repository;

import java.util.UUID;

public interface ProblemRepositoryCustom {

    ProblemDto getProblem(UUID contestId, Long noOfProblemIdInContest);
}
