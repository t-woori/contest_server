package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.domain.problem.exceptions.NotFoundProblemException;
import com.twoori.contest_server.domain.problem.repository.ContentDto;
import com.twoori.contest_server.domain.problem.repository.ProblemCondition;
import com.twoori.contest_server.domain.problem.repository.ProblemDto;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @InjectMocks
    private ProblemService problemService;
    @Mock
    private ProblemRepository problemRepository;

    @DisplayName("대회 ID와 문제 ID를 받아서 문제를 제공|Success|문제 제공 성공")
    @MethodSource("com.twoori.contest_server.domain.problem.repository.Parameters#parametersOfExistsProblemId")
    @ParameterizedTest
    void giveProblemIdWhenGetProblemThenReturnProblem(UUID contestId, Long noOfProblemInContest) {
        // given
        ProblemCondition condition = new ProblemCondition();
        condition.setContestId(contestId);
        condition.setNoOfProblemInContest(noOfProblemInContest);
        ProblemDto expect = new ProblemDto(0L, PROBLEM_TYPE.BLANK, CHAPTER_TYPE.CAFFEE, GRADE.ELEMENTARY, "imageURL", List.of(
                new ContentDto(0L, "answer", "preScript", "question", "postScript", "hint")));
        given(problemRepository.getProblem(condition)).willReturn(expect);

        // when
        ProblemDto actual = problemService.getProblem(contestId, noOfProblemInContest);

        // then
        assertEquals(actual, expect);
    }

    @DisplayName("대회 ID와 문제 ID를 받아서 문제를 제공|Fail|문제 제공 실패")
    @MethodSource("com.twoori.contest_server.domain.problem.repository.Parameters#parametersOfNotExistsProblemId")
    @ParameterizedTest
    void givenProblemIdWhenThrowNotFoundProblemExceptionThenReturnNotFoundProblemException(UUID contestId, Long noOfProblemInContest) {
        // given
        ProblemCondition condition = new ProblemCondition();
        condition.setContestId(contestId);
        condition.setNoOfProblemInContest(noOfProblemInContest);
        given(problemRepository.getProblem(condition)).willThrow(new NotFoundProblemException(contestId, noOfProblemInContest));

        // when & then
        assertThrows(NotFoundProblemException.class, () -> problemService.getProblem(contestId, noOfProblemInContest));

    }

}