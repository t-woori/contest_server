package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;

import java.util.Optional;
import java.util.UUID;

public interface ContestRepositoryCustom {
    Optional<EnterContestDto> getRegisteredStudentAboutStudent(UUID contestId, UUID studentId);

    boolean isResigned(UUID contestId, UUID studentId);


    void updateEnterStudentInContest(UUID studentId, UUID contestId);

    boolean isEnteredStudentInContest(UUID studentId, UUID contestId);
}
