package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dto.LogStudentInProblemIdDto;
import com.twoori.contest_server.domain.problem.dto.QuizScoreDto;

import java.util.Optional;

public interface LogStudentInProblemRepositoryCustom {

    Optional<QuizScoreDto> findByNonPassedProblem(
            LogStudentInProblemIdDto logStudentInProblemIdDto,
            double conditionPassProblem
    );


}
