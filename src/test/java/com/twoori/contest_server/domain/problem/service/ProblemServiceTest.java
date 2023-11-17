package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dao.ContentCompositeID;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import com.twoori.contest_server.domain.problem.dao.ProblemInContest;
import com.twoori.contest_server.domain.problem.dto.ContentDto;
import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.dto.ProblemInContestDto;
import com.twoori.contest_server.domain.problem.dto.SolvedProblemDto;
import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.domain.problem.exceptions.NotFoundProblemException;
import com.twoori.contest_server.domain.problem.mapper.ProblemInContestMapper;
import com.twoori.contest_server.domain.problem.repository.LogStudentInProblemRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemInContestRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @InjectMocks
    private ProblemService problemService;
    @Mock
    private ProblemRepository problemRepository;
    @Mock
    private ProblemInContestRepository problemInContestRepository;
    @Mock
    private LogStudentInProblemRepository logStudentInProblemRepository;
    @Mock
    private ProblemInContestMapper problemInContestMapper;


    @DisplayName("대회 ID와 문제 ID를 받아서 문제를 제공|Success|문제 제공 성공")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#argumentsOfExistsProblemId")
    @ParameterizedTest
    void giveProblemId_whenGetProblem_thenReturnProblem(UUID contestId, Long noOfProblemInContest) {
        // given
        ProblemDto expect = new ProblemDto(0L, PROBLEM_TYPE.BLANK, CHAPTER_TYPE.CAFFEE, GRADE.ELEMENTARY, "imageURL", List.of(
                new ContentDto(0L, "answer", "preScript", "question", "postScript", "hint")));
        given(problemRepository.getProblem(contestId, noOfProblemInContest)).willReturn(expect);

        // when
        ProblemDto actual = problemService.getProblem(contestId, noOfProblemInContest);

        // then
        assertEquals(actual, expect);
    }

    @DisplayName("대회 ID와 문제 ID를 받아서 문제를 제공|Fail|문제 제공 실패")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#argumentsOfNotExistsProblemId")
    @ParameterizedTest
    void givenProblemId_whenThrowNotFoundProblemException_thenReturnNotFoundProblemException(UUID contestId, Long noOfProblemInContest) {
        // given
        given(problemRepository.getProblem(contestId, noOfProblemInContest)).willThrow(new NotFoundProblemException(contestId, noOfProblemInContest));

        // when & then
        assertThrows(NotFoundProblemException.class, () -> problemService.getProblem(contestId, noOfProblemInContest));

    }

    @DisplayName("처음으로 푸는 문제|Cache Put|RDB에 저장 하고 redis에 캐싱")
    @Test
    void givenNewSolvedProblemVO_whenUpdateScore_thenUpdateMaxScore() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        Long noOfProblemInContest = 0L;
        Long contentId = 0L;
        Double newScore = 0.70;
        SolvedProblemDto solvedProblemDto = new SolvedProblemDto(contestId, studentId, noOfProblemInContest, contentId, newScore);
        given(logStudentInProblemRepository.getMaxCountOfTryAboutId(
                LogStudentInProblemID.ofExcludeCountOfTry(contestId, studentId, noOfProblemInContest, solvedProblemDto.contentId())
        )).willReturn(0);

        // when
        Double actual = problemService.updateMaxScoreAboutProblem(solvedProblemDto);

        // then
        verify(logStudentInProblemRepository).save(
                new LogStudentInProblem(
                        LogStudentInProblemID.ofIncludeCountOfTry(contestId, studentId, noOfProblemInContest, solvedProblemDto.contentId(), 1)
                        , solvedProblemDto.newScore()));
        assertThat(actual).isEqualTo(newScore);
    }

    @DisplayName("기존 점수보다 새로운 문제 점수가 높다|Not Cache Put|RDB에 저장하고 redis에 캐싱")
    @Test
    void givenNewScore_whenUpdateScore_thenUpdateMaxScore() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        Long noOfProblemInContest = 0L;
        Long contentId = 0L;
        Double newScore = 0.70;
        LogStudentInProblemID id = LogStudentInProblemID.ofExcludeCountOfTry(contestId, studentId, noOfProblemInContest, contentId);
        SolvedProblemDto solvedProblemDto = new SolvedProblemDto(contestId, studentId, noOfProblemInContest, contentId, newScore);
        given(logStudentInProblemRepository.getMaxCountOfTryAboutId(id)).willReturn(1);
        given(logStudentInProblemRepository.getMaxScoreProblemOne(id)).willReturn(0.60);

        // when
        Double actual = problemService.updateMaxScoreAboutProblem(solvedProblemDto);

        // then
        Integer nextCountOfTry = 0;
        LogStudentInProblemID insertedEntityId = LogStudentInProblemID.ofIncludeCountOfTry(contestId,
                studentId,
                noOfProblemInContest,
                contentId,
                nextCountOfTry);
        assertThat(actual).isEqualTo(newScore);
        verify(logStudentInProblemRepository, times(1)).save(
                new LogStudentInProblem(insertedEntityId, solvedProblemDto.newScore()));
    }

    @DisplayName("기존 점수보다 새로운 점수가 더 낮다|Not Cache Put|RDB에 로그만 기록하고 rediss에 캐싱하지 않는다.")
    @Test
    void givenNewScore_whenUpdateScore_thenNotUpdateMaxScore() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        Long noOfProblemInContest = 0L;
        Long contentId = 0L;
        Double beforeMaxScore = 0.70;
        Double newScore = 0.60;
        LogStudentInProblemID id = LogStudentInProblemID.ofExcludeCountOfTry(contestId, studentId, noOfProblemInContest, contentId);
        SolvedProblemDto solvedProblemDto = new SolvedProblemDto(contestId, studentId, noOfProblemInContest, contentId, newScore);
        given(logStudentInProblemRepository.getMaxCountOfTryAboutId(id)).willReturn(1);
        given(logStudentInProblemRepository.getMaxScoreProblemOne(id)).willReturn(beforeMaxScore);

        // when
        Double actual = problemService.updateMaxScoreAboutProblem(solvedProblemDto);

        // then
        Integer nextCountOfTry = 1;
        LogStudentInProblemID insertedEntityId = LogStudentInProblemID.ofIncludeCountOfTry(contestId,
                studentId,
                noOfProblemInContest,
                contentId,
                nextCountOfTry);
        assertThat(actual).isEqualTo(beforeMaxScore);
        verify(logStudentInProblemRepository, times(1)).save(
                new LogStudentInProblem(insertedEntityId, solvedProblemDto.newScore()));
    }

    @DisplayName("대회 평균 점수 계산|Success|대회 n개의 문제를 풀고 평균을 반환")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#argumentsOfMaxScoreAboutProblems")
    @ParameterizedTest(name = "점수 {1} 가 산출")
    void givenStudentInContestId_whenCreateAverageScore_thenReturnAverageScore(List<Double> maxProblemsScore, double expect) {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        List<ProblemInContest> problemInContests = new ArrayList<>();
        for (int i = 0; i < maxProblemsScore.size(); i++) {
            problemInContests.add(null);
        }
        given(problemInContestRepository.findById_ContestId(contestId)).willReturn(problemInContests);
        given(problemInContestMapper.toDto(isNull())).willReturn(
                new ProblemInContestDto(LongStream
                        .range(0, maxProblemsScore.size())
                        .mapToObj(v -> new ContentCompositeID(v, 0L))
                        .toList()));
        for (long i = 0; i < maxProblemsScore.size(); i++) {
            given(logStudentInProblemRepository.getMaxScoreProblemOne(
                    LogStudentInProblemID.ofExcludeCountOfTry(contestId, studentId, i, 0L)))
                    .willReturn(maxProblemsScore.get((int) i));
        }

        // when
        double actual = problemService.createAverageScore(contestId, studentId);

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("문제를 하나도 풀지 않고 대회 평균 점수를 계산|Success|0을 반환")
    @Test
    void givenNotSolvedProblem_whenCreateAverageScore_thenReturnZero() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        // when
        double actual = problemService.createAverageScore(contestId, studentId);

        // then
        assertThat(actual).isZero();

    }
}