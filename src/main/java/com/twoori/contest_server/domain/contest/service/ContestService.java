package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ContestService {
    private static final int ENTER_TIME = 10;
    private final ContestRepository contestRepository;

    public ContestService(ContestRepository repository) {
        this.contestRepository = repository;
    }

    public boolean isEnterContest(UUID contestId, LocalDateTime now) {
        Contest contest = contestRepository.findById(contestId).orElseThrow(RuntimeException::new);
        LocalDateTime enterTime = contest.getRunningStartDateTime().minusMinutes(ENTER_TIME);
        LocalDateTime endTime = contest.getRunningEndDateTime();
        return enterTime.isEqual(now) || enterTime.isAfter(now) && endTime.isBefore(now);
    }
}
