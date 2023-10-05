package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.repository.LogStudentInProblemRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import com.twoori.contest_server.domain.problem.vo.SolvedProblemVO;
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
    private final LogStudentInProblemRepository logStudentInProblemRepository;
    private final ProblemRepository problemRepository;

    private final Map<Long, Long> totalStatus = new HashMap<>();


    public ProblemService(ProblemRepository problemRepository,
                          LogStudentInProblemRepository logStudentInProblemRepository) {
        this.problemRepository = problemRepository;
        this.logStudentInProblemRepository = logStudentInProblemRepository;
    }

    public List<Long> getTotalStatus() {
        return List.copyOf(totalStatus.values());
    }

    @Cacheable(value = "problem", key = "#contestId.toString() +'_' +#noOfProblemInContest.toString()")
    public ProblemDto getProblem(UUID contestId, Long noOfProblemInContest) {
        return problemRepository.getProblem(contestId, noOfProblemInContest);
    }

    public Double updateMaxScoreAboutProblem(SolvedProblemVO solvedProblemVO) {
        LogStudentInProblemID logStudentInProblemID = LogStudentInProblemID.ofExcludeCountOfTry(
                solvedProblemVO.contestId(),
                solvedProblemVO.studentId(),
                solvedProblemVO.noOfProblemInContest(),
                solvedProblemVO.contentId());
        Integer maxCount = logStudentInProblemRepository.countLatestSolvedProblem(logStudentInProblemID);
        if (maxCount == null) {
            maxCount = 0;
        }
        logStudentInProblemRepository.save(
                new LogStudentInProblem(
                        LogStudentInProblemID.ofIncludeCountOfTry(
                                solvedProblemVO.contestId(),
                                solvedProblemVO.studentId(),
                                solvedProblemVO.noOfProblemInContest(),
                                solvedProblemVO.contentId(),
                                maxCount + 1)
                        , solvedProblemVO.newScore()));
        Double maxScore = logStudentInProblemRepository.getMaxScoreProblemOne(logStudentInProblemID);
        if (maxScore < solvedProblemVO.newScore()) {
            return solvedProblemVO.newScore();
        }
        return maxScore;
    }
}
