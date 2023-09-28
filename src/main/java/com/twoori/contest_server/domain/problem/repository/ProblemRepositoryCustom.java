package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;

import java.util.UUID;

public interface ProblemRepositoryCustom {

    ProblemDto getProblem(UUID contestId, Long noOfProblemIdInContest);
}
