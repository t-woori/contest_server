package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
    }

    public List<Long> getTotalStatus() {
        return List.copyOf(totalStatus.values());
    }

    @Cacheable(value = "problem", key = "#contestId.toString() +'_' +#noOfProblemInContest.toString()")
    public ProblemDto getProblem(UUID contestId, Long noOfProblemInContest) {
        return problemRepository.getProblem(contestId, noOfProblemInContest);
    }

}
