package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.*;
import com.twoori.contest_server.domain.contest.excpetion.*;
import com.twoori.contest_server.domain.contest.mapper.ContestDtoForControllerMapper;
import com.twoori.contest_server.domain.contest.mapper.RepositoryMapper;
import com.twoori.contest_server.domain.contest.repository.ContestCondition;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.dto.StudentInContestDto;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.global.exception.BadRequestException;
import com.twoori.contest_server.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ContestService {
    private static final int ENTER_TIME = 10;
    private final StudentInContestRepository studentInContestRepository;
    private final ContestRepository contestRepository;
    private final ContestDtoForControllerMapper mapper;
    private final RepositoryMapper repositoryMapper;
    public ContestService(StudentInContestRepository studentInContestRepository,
                          ContestRepository contestRepository,
                          ContestDtoForControllerMapper mapper, RepositoryMapper repositoryMapper) {
        this.studentInContestRepository = studentInContestRepository;
        this.contestRepository = contestRepository;
        this.mapper = mapper;
        this.repositoryMapper = repositoryMapper;
    }

    public EnterContestDtoForController enterStudentInContest(UUID studentId, UUID contestId, LocalDateTime enterDateTime) {
        EnterContestDto contest = contestRepository.getRegisteredStudentAboutStudent(contestId, studentId)
                .orElseThrow(() -> new NotFoundRegisteredContestException(studentId, contestId));
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
        ContestCondition condition = new ContestCondition();
        condition.setParameter(parameter);
        condition.setFrom(from.atStartOfDay());
        condition.setTo(to.atTime(23, 59, 59));
        List<SearchContestDto> result = contestRepository.searchNotStartedContests(condition);
        return mapper.toSearchDtoForControllerList(result);

    }

    public Set<UUID> getRegisteredContestIdsInFromTo(UUID studentId, LocalDate from, LocalDate to) {
        ContestCondition condition = new ContestCondition();
        condition.setRegisteredStudentId(studentId);
        condition.setFrom(from.atStartOfDay());
        condition.setTo(to.atStartOfDay());
        return contestRepository.getContestIdSetAboutRegisteredStudent(condition);
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
                        .isResigned(false)
                        .isEntered(false)
                        .build()
        );
    }

    public List<RegisteredContestDto> searchContestForEnterContest(UUID studentId) {
        LocalDateTime start = LocalDateTime.now().minusMinutes(1);
        LocalDateTime end = start.plusMonths(3);
        ContestCondition condition = new ContestCondition();
        condition.setRegisteredStudentId(studentId);
        condition.setFrom(start);
        condition.setTo(end);
        List<SearchContestDto> searchContestDtos = new ArrayList<>(contestRepository.searchRegisteredContest(condition));
        searchContestDtos.sort(Comparator.comparing(SearchContestDto::startedAt)
                .thenComparing(SearchContestDto::endedAt));
        return mapper.toRegisteredContestDto(searchContestDtos);
    }

    public void cancelContest(UUID contestId, UUID studentId, LocalDateTime cancelTime) {
        CancelContestDto contestDto = contestRepository.getTimesAboutContest(contestId)
                .orElseThrow(() -> new NotFoundRegisteredContestException(studentId, contestId));
        LocalDateTime expiredTime = contestDto.startDateTime().toLocalDate().atStartOfDay();
        if (cancelTime.isBefore(expiredTime) || cancelTime.isEqual(expiredTime)) {
            contestRepository.cancelContest(contestId, studentId);
            return;
        }
        throw new NotCancelRegisterContest(studentId, contestId);
    }

    public void resignContest(UUID contestId, UUID studentId) {
        if (!contestRepository.isEnteredStudentInContest(studentId, contestId)) {
            log.error("not entered contest, contestId: {}, studentId: {}", contestId, studentId);
            throw new NotRegisteredContestException(studentId, contestId);
        }
        contestRepository.resignContest(contestId, studentId);
    }

    public List<SearchContestDto> searchEndOfContests(UUID studentIdAboutRegisteredContest) {
        ContestCondition condition = new ContestCondition();
        condition.setRegisteredStudentId(studentIdAboutRegisteredContest);
        condition.setFrom(LocalDateTime.now().minusMonths(3));
        condition.setTo(LocalDateTime.now());
        List<SearchContestDto> searchContestDtos = new ArrayList<>(contestRepository.searchEndOfContests(condition));
        searchContestDtos.sort(Comparator.comparing(SearchContestDto::startedAt)
                .thenComparing(SearchContestDto::endedAt));
        return searchContestDtos;
    }

    public long endingContest(UUID contestId, UUID studentId, LocalDateTime endDateTime) {
        StudentInContest studentInContest = studentInContestRepository.findById(new StudentInContestID(studentId, contestId))
                .orElseThrow(() -> new NotFoundRegisteredContestException(studentId, contestId));
        StudentInContestDto studentInContestDto = repositoryMapper.toStudentInContestDto(studentInContest);
        if (studentInContestDto.endContestAt() != null) {
            return Duration.between(studentInContestDto.startedAt(),
                    studentInContestDto.endContestAt()).toSeconds();
        }
        LocalDateTime loggedEndDateTime = endDateTime;
        if (endDateTime.isAfter(studentInContestDto.endContestDateTime())) {
            loggedEndDateTime = studentInContestDto.endContestDateTime();
        }
        studentInContest.setEndContestAt(loggedEndDateTime);
        studentInContestRepository.save(studentInContest);
        return Duration.between(studentInContestDto.startedAt(), loggedEndDateTime).toSeconds();
    }

    public UUID findContestIdAboutEnterableContest(UUID studentId, LocalDateTime now) {
        return studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTime(studentId, now)
                .orElseThrow(() -> new NotFoundRegisteredContestException(studentId, null))
                .getId().getContestID();
    }
}
