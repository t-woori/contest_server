package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;

public interface LogStudentInProblemRepositoryCustom {
    Integer getMaxCountOfTryAboutId(LogStudentInProblemID logStudentInProblemID);

    Double getMaxScoreProblemOne(LogStudentInProblemID logStudentInProblemID);
}
