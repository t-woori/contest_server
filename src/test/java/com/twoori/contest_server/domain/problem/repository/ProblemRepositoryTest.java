package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dto.ContentDto;
import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.domain.problem.exceptions.NotFoundProblemException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@DataJpaTest
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @DisplayName("존재하는 문제 검색|Success|여러개의 contenet가 포함된 문제 단건 조회")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#parametersOfExistsProblemId")
    @ParameterizedTest(name = "contestId: {0}, noOfProblemInContest: {1}")
    void givenExistProblemId_whenGetProblem_thenOneProblem(UUID contestId, Long noOfProblemIdInContest) {
        // when
        ProblemDto actual = problemRepository.getProblem(contestId, noOfProblemIdInContest);

        // then
        assertThat(actual)
                .isNotNull()
                .extracting(ProblemDto::problemId, ProblemDto::problemType, ProblemDto::chapterType, ProblemDto::problemGrade)
                .isNotNull().isNotEmpty();
        assertThat(actual.contents()).hasSize(1)
                .extracting(ContentDto::answer, ContentDto::question)
                .isNotNull().isNotEmpty();
        if (actual.problemType() == PROBLEM_TYPE.BLANK) {
            assertThat(actual.imageURL()).isNotNull().isNotEmpty();
        }
    }

    @DisplayName("존재하지 않는 문제 검색|Fail|NotFoundProblemException")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#parametersOfNotExistsProblemId")
    @ParameterizedTest(name = "contestId: {0}, noOfProblemInContest: {1}")
    void givenNotExistProblemIdId_whenGetProblem_thenThrowNotFoundProblemException(UUID contestId, Long noOfProblemIdInContest) {
        // given
        ProblemCondition condition = new ProblemCondition();
        condition.setNoOfProblemInContest(noOfProblemIdInContest);
        condition.setContestId(contestId);

        // when & then
        NotFoundProblemException exception = assertThrows(NotFoundProblemException.class, () -> problemRepository.getProblem(
                contestId, noOfProblemIdInContest
        ));
        assertThat(exception)
                .isNotNull()
                .extracting(NotFoundProblemException::getMessage)
                .isNotNull().isEqualTo("not found problem");
        assertThat(exception.getParamsInfo())
                .isNotNull()
                .isEqualTo("contestId: " + contestId + " noOfProblemInContest: " + noOfProblemIdInContest);
    }

}