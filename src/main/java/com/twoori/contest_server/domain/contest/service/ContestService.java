package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.ContestDto;
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
        StudentInContest studentInContest = studentInContestRepository.findByContest_IdAndStudent_Id(contestId, studentId)
                .orElseThrow(() -> new NotFoundException("not register contest"));
        Contest contest = studentInContest.getContest();
        if (enterDateTime.isAfter(contest.getRunningEndDateTime())) {
            throw new BadRequestException("expired contest");
        }
        if (enterDateTime.isBefore(contest.getRunningStartDateTime().minusMinutes(ENTER_TIME))) {
            throw new BadRequestException("early contest");
        }
        return null;
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
