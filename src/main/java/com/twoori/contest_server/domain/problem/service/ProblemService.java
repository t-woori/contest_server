package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dao.*;
import com.twoori.contest_server.domain.problem.dto.ContentDto;
import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.dto.ProblemInContestDto;
import com.twoori.contest_server.domain.problem.repository.LogStudentInProblemRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemInContestRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import com.twoori.contest_server.global.exception.OKException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final LogStudentInProblemRepository logStudentInProblemRepository;
    private final ProblemInContestRepository problemInContestRepository;

    private final long LAST_PROBLEM_NO_IN_CONTEST = 9L;

    public ProblemService(ProblemRepository problemRepository, LogStudentInProblemRepository logStudentInProblemRepository, ProblemInContestRepository problemInContestRepository) {
        this.problemRepository = problemRepository;
        this.logStudentInProblemRepository = logStudentInProblemRepository;
        this.problemInContestRepository = problemInContestRepository;
    }

    public ProblemDto getProblemByStudent(UUID contestId, UUID studentId) {
        try {
            long lastProblemNo = getLastProblemNo(contestId, studentId);
            if (lastProblemNo >= LAST_PROBLEM_NO_IN_CONTEST) {
                throw new OKException("end contest");
            }
            long nextProblemNo = lastProblemNo + 1;
            saveStartNewProblem(contestId, studentId, nextProblemNo);
            return getLastProblemByStudentInContest(contestId, nextProblemNo);
        } catch (NotSolvedProblemException e) {
            return getLastProblemByStudentInContest(contestId, e.getNoSolvedProblemNo());
        } catch (FirstSolveException e) {
            saveStartNewProblem(contestId, studentId, 0L);
            return getLastProblemByStudentInContest(contestId, 0L);
        }
    }

    private Long getLastProblemNo(UUID contestId, UUID studentId) {
        LogStudentInProblem log = logStudentInProblemRepository.findLastNoOfProblemInContest(contestId, studentId)
                .orElseThrow(FirstSolveException::new);
        if (log.getEndSolveProblemDateTime() == null) {
            throw new NotSolvedProblemException(log.getLogStudentInProblemId().getNoOfProblemInContest());
        }
        return log.getLogStudentInProblemId().getNoOfProblemInContest();
    }


    private ProblemDto getLastProblemByStudentInContest(UUID contestId, Long lastProblemNo) {
        List<ProblemInContestDto> problemLists = problemInContestRepository.findById_ContestIdOrderByNoOfProblemInContestAsc(contestId)
                .stream().map(problemInContest -> new ProblemInContestDto(problemInContest.getId().getProblemId(),
                        problemInContest.getId().getContestId(), problemInContest.getNoOfProblemInContest()))
                .toList();
        Long nextProblemID = problemLists.get(lastProblemNo.intValue() + 1).problemID();

        Problem problemDao = problemRepository.findById(nextProblemID)
                .orElseThrow(() -> new IllegalArgumentException("not found problem"));
        return new ProblemDto(problemDao.getId(),
                problemDao.getImageURL(), problemDao.getGrade(), problemDao.getProblemType(),
                problemDao.getContents().stream()
                        .map(content -> new ContentDto(content.getContentCompositeId().getProblemId(),
                                content.getContentCompositeId().getContentId(), content.getPreScript(),
                                content.getQuestion(), content.getAnswer(),
                                content.getPostScript())).toList()
                , problemDao.getChapterType());
    }

    private void saveStartNewProblem(UUID contestId, UUID studentId, Long nextProblemNo) {
        logStudentInProblemRepository.save(new LogStudentInProblem(
                new LogStudentInProblemID(contestId, studentId, nextProblemNo),
                LocalDateTime.now()
        ));
    }

    public void updateQuizStatus(UUID contestId, UUID studentId, Long problemId) {
        long noOfProblemInContest = problemInContestRepository.findById(new ProblemInContestId(problemId, contestId))
                .map(ProblemInContest::getNoOfProblemInContest).orElseThrow(() -> new IllegalArgumentException("not found problem"));
        logStudentInProblemRepository.findById(
                new LogStudentInProblemID(contestId, studentId, noOfProblemInContest)
        ).ifPresent(log -> {
                    log.setEndSolveProblemDateTime(LocalDateTime.now());
                    logStudentInProblemRepository.save(log);
                }
        );
    }
}
