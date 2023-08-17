package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import com.twoori.contest_server.domain.problem.dao.Problem;
import com.twoori.contest_server.domain.problem.dto.*;
import com.twoori.contest_server.domain.problem.exceptions.FirstSolveException;
import com.twoori.contest_server.domain.problem.exceptions.NotSolvedProblemException;
import com.twoori.contest_server.domain.problem.repository.LogStudentInProblemRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemInContestRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import com.twoori.contest_server.global.exception.NotFoundException;
import com.twoori.contest_server.global.exception.OKException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final LogStudentInProblemRepository logStudentInProblemRepository;
    private final ProblemInContestRepository problemInContestRepository;
    private final int PASS_PROBLEM_CONDITION = 60;
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
            // TODO: 임시값이므로 구현시 제거 요망
            throw new NotSolvedProblemException(0L);
        }
        // TODO: 임시값이므로 구현시 제거 요망
        return 0L;
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
                new LogStudentInProblemID(contestId, studentId, nextProblemNo, 0L),
                LocalDateTime.now()
        ));
    }

    public void updateQuizScore(QuizScoreDto quizScoreDto) {
        log.info("[Service] quizScoreDto : {}", quizScoreDto);
        QuizScoreDto notSolvedLog = logStudentInProblemRepository.findByNonPassedProblem(
                new LogStudentInProblemIdDto(
                        quizScoreDto.contestId(),
                        quizScoreDto.studentId(),
                        quizScoreDto.problemId(),
                        quizScoreDto.contentId()
                ), PASS_PROBLEM_CONDITION
        ).orElseThrow(() -> new NotFoundException("not found not solved problem"));
        logStudentInProblemRepository.findById(
                new LogStudentInProblemID(
                        quizScoreDto.contestId(),
                        quizScoreDto.studentId(),
                        quizScoreDto.problemId(),
                        quizScoreDto.contentId()
                )
        ).ifPresent(logStudentInProblem -> {
            log.info("[Service] found logStudentInProblem : {}", logStudentInProblem);
            if (quizScoreDto.score() <= notSolvedLog.score()) {
                log.info("[Service] not update score: requestScore {} savedScore {}",
                        quizScoreDto.score(),
                        notSolvedLog.score());
                throw new OKException("not update score");
            }
            log.info("[Service] update score : {}", quizScoreDto.score());
            logStudentInProblem.setEndSolveProblemDateTime(LocalDateTime.now());
            logStudentInProblem.setScore(quizScoreDto.score());
            logStudentInProblemRepository.save(logStudentInProblem);
        });
        log.error("[Service] not found logStudentInProblem : {}", notSolvedLog);
    }
}
