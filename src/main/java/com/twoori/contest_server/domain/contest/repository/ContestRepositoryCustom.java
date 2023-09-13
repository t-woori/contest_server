package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;

import java.util.UUID;

public interface ContestRepositoryCustom {
    EnterContestDto getRegisteredStudentAboutStudent(UUID contestId, UUID studentId);

    boolean isResigned(UUID contestId, UUID studentId);
}