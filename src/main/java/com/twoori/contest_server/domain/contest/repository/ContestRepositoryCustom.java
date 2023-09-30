package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dto.CancelContestDto;
import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ContestRepositoryCustom {
    Optional<EnterContestDto> getRegisteredStudentAboutStudent(UUID contestId, UUID studentId);

    boolean isResigned(UUID contestId, UUID studentId);


    void updateEnterStudentInContest(UUID studentId, UUID contestId);

    boolean isEnteredStudentInContest(UUID studentId, UUID contestId);

    List<SearchContestDto> getContestsHasParameterInName(String parameter, LocalDateTime from, LocalDateTime to);

    Set<UUID> getContestIdSetAboutRegisteredStudent(UUID id, LocalDate from, LocalDate to);

    List<RegisteredContestDto> getRegisteredContestsInFromTo(UUID studentId, LocalDateTime from, LocalDateTime to);

    void cancelContest(UUID contestId, UUID studentId);

    Optional<CancelContestDto> getTimesAboutContest(UUID contestId);
}
