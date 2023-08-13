package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.global.exception.BadRequestException;
import com.twoori.contest_server.global.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ContestService {
    private static final int ENTER_TIME = 10;
    private final StudentInContestRepository studentInContestRepository;

    public ContestService(StudentInContestRepository studentInContestRepository) {
        this.studentInContestRepository = studentInContestRepository;
    }

    public ContestDto getAccessibleContest(UUID studentId, UUID contestId, LocalDateTime enterDateTime) {
        StudentInContest studentInContest = studentInContestRepository.findByContest_IdAndStudent_Id(contestId, studentId)
                .orElseThrow(() -> new NotFoundException("not register contest"));
        Contest contest = studentInContest.getContest();
        if (enterDateTime.isAfter(contest.getRunningEndDateTime())) {
            throw new BadRequestException("expired contest");
        }
        if (enterDateTime.isBefore(contest.getRunningStartDateTime().minusMinutes(ENTER_TIME))) {
            throw new BadRequestException("early contest");
        }
        return ContestDto.daoToDto(contest);
    }

}
