package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.*;
import com.twoori.contest_server.domain.contest.excpetion.*;
import com.twoori.contest_server.domain.contest.mapper.ContestDtoForControllerMapper;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.global.exception.BadRequestException;
import com.twoori.contest_server.global.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ContestService {
    private static final int ENTER_TIME = 10;
    private final StudentInContestRepository studentInContestRepository;
    private final ContestRepository contestRepository;
    private final ContestDtoForControllerMapper mapper;

    public ContestService(StudentInContestRepository studentInContestRepository,
                          ContestRepository contestRepository,
                          ContestDtoForControllerMapper mapper) {
        this.studentInContestRepository = studentInContestRepository;
        this.contestRepository = contestRepository;
        this.mapper = mapper;
    }

    public EnterContestDtoForController enterStudentInContest(UUID studentId, UUID contestId, LocalDateTime enterDateTime) {
        EnterContestDto contest = contestRepository.getRegisteredStudentAboutStudent(contestId, studentId)
                .orElseThrow(() -> new NotFoundContestException(studentId, contestId));
        checkEnterTimeInContest(studentId, enterDateTime, contest);
        if (contestRepository.isResigned(contestId, studentId)) {
            throw new ResignedContestException(studentId, contest);
        }
        if (enterDateTime.isAfter(contest.startDateTime().plusMinutes(1).plusSeconds(1)) &&
                !contestRepository.isEnteredStudentInContest(studentId, contestId)) {
            throw new ExpiredTimeException(studentId, contest);
        }
        contestRepository.updateEnterStudentInContest(studentId, contestId);
        return mapper.toEnterContestDtoForController(contest);
    }

    private void checkEnterTimeInContest(UUID studentId, LocalDateTime enterDateTime, EnterContestDto contest) {
        if (enterDateTime.isAfter(contest.endDateTime())) {
            throw new EndContestException(studentId, contest);
        }
        if (enterDateTime.isBefore(contest.startDateTime().minusMinutes(ENTER_TIME))) {
            throw new EarlyEnterTimeException(studentId, contest);
        }
    }

    public List<SearchContestDtoForController> searchContests(String parameter, LocalDate from, LocalDate to) {
        LocalDateTime now = LocalDateTime.now();
        if (from.isAfter(to) || from.isBefore(now.toLocalDate())) {
            return new ArrayList<>();
        }
        return contestRepository.getContestsHasParameterInName(parameter,
                        from.atTime(now.toLocalTime()),
                        to.atTime(now.toLocalTime()))
                .stream().sorted(
                        Comparator.comparing(SearchContestDto::runningStartDateTime)
                                .thenComparing(SearchContestDto::runningEndDateTime)
                ).map(mapper::toSearchDtoForController).toList();
    }

    public Set<UUID> getRegisteredContestIdsInFromTo(UUID studentId, LocalDate from, LocalDate to) {
        return contestRepository.getContestIdSetAboutRegisteredStudent(studentId, from, to);
    }

    public void registerContestByUser(UUID contestId, StudentDto studentDto, String authCode) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new NotFoundException("not found contest"));
        if (!contest.getAuthCode().equals(authCode)) {
            throw new BadRequestException("not match auth code");
        }
        studentInContestRepository.save(
                StudentInContest.builder()
                        .id(new StudentInContestID(studentDto.id(), contestId))
                        .build()
        );
    }

    public List<RegisteredContestDto> getRegisteredContestsInFromTo(UUID studentId) {
        LocalDateTime start = LocalDateTime.now().minusMinutes(1);
        LocalDateTime end = start.plusMonths(3);
        return contestRepository.getRegisteredContestsInFromTo(studentId, start, end);
    }

    public void cancelContest(UUID contestId, UUID studentId, LocalDateTime cancelTime) {
        CancelContestDto contestDto = contestRepository.getTimesAboutContest(contestId)
                .orElseThrow(() -> new NotFoundContestException(studentId, contestId));
        LocalDateTime expiredTime = contestDto.startDateTime().toLocalDate().atStartOfDay();
        if (cancelTime.isBefore(expiredTime) || cancelTime.isEqual(expiredTime)) {
            contestRepository.cancelContest(contestId, studentId);
            return;
        }
        throw new NotCancelRegisterContest(studentId, contestId);
    }

    public void resignContest(UUID contestId, UUID studentId) {
        if (!contestRepository.isEnteredStudentInContest(studentId, contestId)) {
            throw new NotRegisteredContestException(studentId, contestId);
        }
        contestRepository.resignContest(contestId, studentId);
    }
}
