package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;


    private final Map<Long, Long> totalStatus = new HashMap<>();

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
        initTotalStatus();
    }

    private void initTotalStatus() {
        for (long i = 0; i <= 9L; i++) {
            totalStatus.put(i, 0L);
        }
    }

    public List<Long> getTotalStatus() {
        return List.copyOf(totalStatus.values());
    }

    public ProblemDto getProblem(UUID contestId, Long noOfProblemInContest) {
        return problemRepository.getProblem(contestId, noOfProblemInContest);
    }
}
