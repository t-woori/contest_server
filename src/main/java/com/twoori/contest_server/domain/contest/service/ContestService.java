package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.excpetion.EarlyEnterTimeException;
import com.twoori.contest_server.domain.contest.excpetion.ExpiredTimeException;
import com.twoori.contest_server.domain.contest.excpetion.NotFoundContestException;
import com.twoori.contest_server.domain.contest.excpetion.ResignedContestException;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.global.exception.BadRequestException;
import com.twoori.contest_server.global.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContestService {
    private static final int ENTER_TIME = 10;
    private final StudentInContestRepository studentInContestRepository;
    private final ContestRepository contestRepository;

    public ContestService(StudentInContestRepository studentInContestRepository, ContestRepository contestRepository) {
        this.studentInContestRepository = studentInContestRepository;
        this.contestRepository = contestRepository;
    }

    public EnterContestDtoForController enterStudentInContest(UUID studentId, UUID contestId, LocalDateTime enterDateTime) {
        EnterContestDto contest = contestRepository.getRegisteredStudentAboutStudent(contestId, studentId)
                .orElseThrow(() -> new NotFoundContestException(studentId, contestId));
        checkEnterTimeInContest(studentId, enterDateTime, contest);
        if (contestRepository.isResigned(contestId, studentId)) {
            throw new ResignedContestException(studentId, contest);
        }
        return new EnterContestDtoForController(
                contest.contestId(),
                contest.startDateTime(),
                contest.endDateTime()
        );
    }

    private void checkEnterTimeInContest(UUID studentId, LocalDateTime enterDateTime, EnterContestDto contest) {
        if (enterDateTime.isAfter(contest.endDateTime())) {
            throw new ExpiredTimeException(studentId, contest);
        }
        if (enterDateTime.isBefore(contest.startDateTime().minusMinutes(ENTER_TIME))) {
            throw new EarlyEnterTimeException(studentId, contest);
        }
    }

    public List<ContestDto> searchContests(String parameter) {
        return contestRepository.findByNameContains(parameter)
                .stream().map(ContestDto::daoToDto).toList();
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
}
