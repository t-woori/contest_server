package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.dto.ProblemInContestDto;
import com.twoori.contest_server.domain.problem.dto.SolvedProblemDto;
import com.twoori.contest_server.domain.problem.mapper.ProblemInContestMapper;
import com.twoori.contest_server.domain.problem.repository.LogStudentInProblemRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemInContestRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ProblemService {
    private final ProblemInContestMapper problemInContestMapper;
    private final ProblemInContestRepository problemInContestRepository;
    private final LogStudentInProblemRepository logStudentInProblemRepository;
    private final ProblemRepository problemRepository;

    private final Map<Long, Long> totalStatus = new HashMap<>();


    public ProblemService(ProblemRepository problemRepository,
                          LogStudentInProblemRepository logStudentInProblemRepository,
                          ProblemInContestRepository problemInContestRepository,
                          ProblemInContestMapper problemInContestMapper) {
        this.problemRepository = problemRepository;
        this.logStudentInProblemRepository = logStudentInProblemRepository;
        this.problemInContestRepository = problemInContestRepository;
        this.problemInContestMapper = problemInContestMapper;
    }

    public List<Long> getTotalStatus() {
        return List.copyOf(totalStatus.values());
    }

    @Cacheable(value = "problem", key = "#contestId.toString() +'_' +#noOfProblemInContest.toString()")
    public ProblemDto getProblem(UUID contestId, Long noOfProblemInContest) {
        return problemRepository.getProblem(contestId, noOfProblemInContest);
    }

    @CachePut(value = "max_score", key = "#solvedProblemDto.noOfProblemInContest.toString()+'_'+" +
            "#solvedProblemDto.contentId()+'_'+" + "#solvedProblemDto.contestId()+'_' +#solvedProblemDto.studentId")
    public Double updateMaxScoreAboutProblem(SolvedProblemDto solvedProblemDto) {
        LogStudentInProblemID logStudentInProblemID = LogStudentInProblemID.ofExcludeCountOfTry(
                solvedProblemDto.contestId(),
                solvedProblemDto.studentId(),
                solvedProblemDto.noOfProblemInContest(),
                solvedProblemDto.contentId());
        Integer maxCount = logStudentInProblemRepository.getMaxCountOfTryAboutId(logStudentInProblemID);
        logStudentInProblemRepository.save(
                new LogStudentInProblem(
                        LogStudentInProblemID.ofIncludeCountOfTry(
                                solvedProblemDto.contestId(),
                                solvedProblemDto.studentId(),
                                solvedProblemDto.noOfProblemInContest(),
                                solvedProblemDto.contentId(),
                                maxCount + 1)
                        , solvedProblemDto.newScore()));
        Double maxScore = getMaxScore(logStudentInProblemID);
        if (maxScore < solvedProblemDto.newScore()) {
            return solvedProblemDto.newScore();
        }
        return maxScore;
    }

    public Double getMaxScore(LogStudentInProblemID logStudentInProblemID) {
        return logStudentInProblemRepository.getMaxScoreProblemOne(logStudentInProblemID);
    }

    public double createAverageScore(UUID contestId, UUID studentId) {
        return Math.floor(problemInContestRepository.findById_ContestId(contestId)
                .stream()
                .map(problemInContestMapper::toDto)
                .map(ProblemInContestDto::contentContentCompositeIds)
                .flatMap(List::stream)
                .mapToDouble(v ->
                        getMaxScore(LogStudentInProblemID.ofExcludeCountOfTry(
                                contestId, studentId, v.getProblemId(), v.getContentId())))
                .average().orElse(0) * 10000
        ) / 10000;
    }
}
