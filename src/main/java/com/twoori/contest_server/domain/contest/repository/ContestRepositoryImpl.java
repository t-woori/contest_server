package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;

import java.util.UUID;

public class ContestRepositoryImpl implements ContestRepositoryCustom {


    @Override
    public EnterContestDto getRegisteredStudentAboutStudent(UUID contestId, UUID studentId) {
        return null;
    }

    @Override
    public boolean isResigned(UUID contestId, UUID studentId) {
        return false;
    }
}
