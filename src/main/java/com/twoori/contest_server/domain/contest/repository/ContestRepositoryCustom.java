package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dto.CancelContestDto;
import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ContestRepositoryCustom {
    Optional<EnterContestDto> getRegisteredStudentAboutStudent(UUID contestId, UUID studentId);

    boolean isResigned(UUID contestId, UUID studentId);


    void updateEnterStudentInContest(UUID studentId, UUID contestId);

    boolean isEnteredStudentInContest(UUID studentId, UUID contestId);

    Set<UUID> getContestIdSetAboutRegisteredStudent(ContestCondition condition);


    void cancelContest(UUID contestId, UUID studentId);

    Optional<CancelContestDto> getTimesAboutContest(UUID contestId);

    void resignContest(UUID studentId, UUID contestId);

    List<SearchContestDto> searchRegisteredContest(ContestCondition condition);

    List<SearchContestDto> searchEndOfContests(ContestCondition condition);

    List<SearchContestDto> searchNotStartedContests(ContestCondition condition);
}
