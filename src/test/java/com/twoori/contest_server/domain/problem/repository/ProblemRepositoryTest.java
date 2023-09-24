package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.domain.problem.exceptions.NotFoundProblemException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DataJpaTest
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @MethodSource("com.twoori.contest_server.domain.problem.repository.Parameters#parametersOfExistsProblemId")
    @ParameterizedTest
    void givenExistProblemIdWhenGetProblemThenOneProblem(UUID contestId, Long noOfProblemIdInContest) {
        // given problem and contestId When search Problem Then return one problem
        // given
        ProblemCondition condition = new ProblemCondition();
        condition.setNoOfProblemInContest(noOfProblemIdInContest);
        condition.setContestId(contestId);

        // when
        ProblemDto actual = problemRepository.getProblem(condition);

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

    @MethodSource("com.twoori.contest_server.domain.problem.repository.Parameters#parametersOfNotExistsProblemId")
    @ParameterizedTest
    void givenNotExistProblemIdIdWhenGetProblemThenThrowNotFoundProblemException(UUID contestId, Long noOfProblemIdInContest) {
        // given
        ProblemCondition condition = new ProblemCondition();
        condition.setNoOfProblemInContest(noOfProblemIdInContest);
        condition.setContestId(contestId);

        // when & then
        String message = assertThrows(NotFoundProblemException.class, () -> problemRepository.getProblem(condition)).getMessage();
        assertThat(message).isEqualTo("not found contestId: " + contestId + " noOfProblemIdInContest: " + noOfProblemIdInContest);
    }

}