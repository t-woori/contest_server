package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dao.*;
import com.twoori.contest_server.domain.problem.dto.*;
import com.twoori.contest_server.domain.problem.exceptions.FirstSolveException;
import com.twoori.contest_server.domain.problem.exceptions.NotSolvedProblemException;
import com.twoori.contest_server.domain.problem.repository.LogStudentInProblemRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemInContestRepository;
import com.twoori.contest_server.global.exception.NotFoundException;
import com.twoori.contest_server.global.exception.OKException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ProblemService {
    private final ContentRepository contentRepository;

    private final LogStudentInProblemRepository logStudentInProblemRepository;
    private final ProblemInContestRepository problemInContestRepository;
    private final double PASS_PROBLEM_CONDITION = 0.6;
    private final long LAST_PROBLEM_NO_IN_CONTEST = 9L;

    private final long INIT_NO_OF_PROBLEM_IN_CONTEST = 0L;
    private final long INIT_CONTENT_ID = 0L;

    public ProblemService(LogStudentInProblemRepository logStudentInProblemRepository,
                          ProblemInContestRepository problemInContestRepository,
                          ContentRepository contentRepository) {
        this.logStudentInProblemRepository = logStudentInProblemRepository;
        this.problemInContestRepository = problemInContestRepository;
        this.contentRepository = contentRepository;
    }

    public ContentDtoForController getNotSolvedProblemByStudent(MinInfoAboutStudentAndContestDto idInContest) {
        log.info("[Service] get not solved problem contestId : {}, studentId : {}", idInContest.contestId(), idInContest.studentId());
        try {
            InfoLatestProblemDto latestProblemMinInfo = getLatestSolvedProblem(idInContest);
            MinInfoLatestProblemDto nextProblemMinInfo = getNextLatestContentInProblem(idInContest, latestProblemMinInfo);
            saveNewLogProblemInContent(idInContest, nextProblemMinInfo);
            return getDetailLatestProblemInContent(nextProblemMinInfo.getProblemId(), nextProblemMinInfo.getContentId());
        } catch (NotSolvedProblemException e) {
            log.info("[Service] not solved problem contestId : {}, studentId : {}", idInContest.contestId(), idInContest.studentId());
            MinInfoLatestProblemDto notSolvedProblemInfo = e.getLatestProblemInfoDto();
            return getDetailLatestProblemInContent(notSolvedProblemInfo.getProblemId(),
                    notSolvedProblemInfo.getContentId());
        } catch (FirstSolveException e) {
            log.info("[Service] first solved problem contestId : {}, studentId : {}", idInContest.contestId(), idInContest.studentId());
            ProblemInContestDto problemInContestDto = problemInContestRepository.findById_ContestIdAndNoOfProblemInContest(
                    idInContest.contestId(),
                    0L
            ).orElseThrow(() -> new NotFoundException("not found problem in contest"));
            saveNewLogProblemInContent(idInContest, MinInfoLatestProblemDto.of(
                    problemInContestDto.problemId(), INIT_CONTENT_ID, INIT_NO_OF_PROBLEM_IN_CONTEST
            ));
            return getDetailLatestProblemInContent(problemInContestDto.problemId(), INIT_CONTENT_ID);
        } catch (AllSolvedException e) {
            log.info("[Service] all solved problem contestId : {}, studentId : {}", idInContest.contestId(), idInContest.studentId());
            throw new OKException("end contest");
        }
    }

    private InfoLatestProblemDto getLatestSolvedProblem(MinInfoAboutStudentAndContestDto idInContest) {
        log.info("[Service] get last problem no contestId : {}, studentId : {}", idInContest.contestId(), idInContest.studentId());
        InfoLatestProblemDto latestProblemInfoDto = logStudentInProblemRepository.findLatestProblemInContest(
                new MinInfoAboutStudentAndContestWithScoreCondition(idInContest, PASS_PROBLEM_CONDITION)
        ).orElseThrow(FirstSolveException::new);
        if (latestProblemInfoDto.getScore() >= PASS_PROBLEM_CONDITION) {
            return latestProblemInfoDto;
        }
        log.info("[Service] not solved problem contestId : {}, studentId : {}", idInContest.contestId(), idInContest.studentId());
        throw new NotSolvedProblemException(MinInfoLatestProblemDto.of(
                latestProblemInfoDto.getProblemId(),
                latestProblemInfoDto.getContentId(),
                latestProblemInfoDto.getNoOfProblemInContest()
        ));
    }

    private MinInfoLatestProblemDto getNextLatestContentInProblem(MinInfoAboutStudentAndContestDto idInContest, InfoLatestProblemDto latestProblemMinInfo) {
        log.info("[Service] get next problem in content contestId : {}, studentId : {}", latestProblemMinInfo.getContentId(), idInContest.studentId());
        long totalContentsInProblem = contentRepository.countByContentCompositeId_ProblemId(latestProblemMinInfo.getProblemId());
        if (latestProblemMinInfo.getContentId() + 1 < totalContentsInProblem) {
            return new MinInfoLatestProblemDto(
                    latestProblemMinInfo.getProblemId(),
                    latestProblemMinInfo.getContentId() + 1,
                    latestProblemMinInfo.getNoOfProblemInContest()
            );
        }
        if (latestProblemMinInfo.getNoOfProblemInContest() + 1 > LAST_PROBLEM_NO_IN_CONTEST) {
            throw new AllSolvedException();
        }
        ProblemInContestDto problemInContestDto = problemInContestRepository.findById_ContestIdAndNoOfProblemInContest(
                idInContest.contestId(),
                latestProblemMinInfo.getNoOfProblemInContest() + 1
        ).orElseThrow(AllSolvedException::new);


        return new MinInfoLatestProblemDto(
                problemInContestDto.problemId(),
                INIT_CONTENT_ID,
                latestProblemMinInfo.getNoOfProblemInContest()
        );
    }


    private void saveNewLogProblemInContent(MinInfoAboutStudentAndContestDto minInfoAboutStudentAndContestDto,
                                            MinInfoLatestProblemDto nextProblemMinInfo) {
        logStudentInProblemRepository.save(new LogStudentInProblem(
                new LogStudentInProblemID(minInfoAboutStudentAndContestDto.contestId(),
                        minInfoAboutStudentAndContestDto.studentId(),
                        nextProblemMinInfo.getProblemId(),
                        nextProblemMinInfo.getContentId()),
                LocalDateTime.now()
        ));
    }

    private ContentDtoForController getDetailLatestProblemInContent(Long problemId, Long contentId) {
        Content content = contentRepository.findById(new ContentCompositeID(problemId, contentId))
                .orElseThrow(() -> new NotFoundException("not found content"));
        return new ContentDtoForController(
                new ProblemDtoForController(
                        content.getProblem().getId(),
                        content.getProblem().getImageURL(),
                        content.getProblem().getGrade(),
                        content.getProblem().getProblemType(),
                        content.getProblem().getChapterType()
                ),
                content.getContentCompositeId().getContentId(),
                content.getPreScript(),
                content.getQuestion(),
                content.getAnswer(),
                content.getPostScript()
        );
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
    }
}
