package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@DataJpaTest
class LogStudentInProblemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private LogStudentInProblemRepository logStudentInProblemRepository;

    @DisplayName("학생이 문제 푼 기록이 count 건 있다.|Success| count를 반환한다")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    @ParameterizedTest(name = "문제 푼 건수: {0}")
    void givenExistSolvedProblemVo_whenSaveSolvedProblem_thenReturnCount(int count) {
        // given
        UUID contestId = UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f");
        UUID studentId = UUID.fromString("d7762394-592c-4e33-8d71-06fc5a94abfb");
        Long problemId = 1L;
        Long contentId = 0L;
        Double score = 0.01;
        for (int i = 0; i < count; i++) {
            logStudentInProblemRepository.saveAndFlush(new LogStudentInProblem(
                    LogStudentInProblemID.ofIncludeCountOfTry(
                            contestId, studentId, problemId, contentId, i + 1)
                    , score));
        }

        // when
        Integer maxCount = logStudentInProblemRepository.getMaxCountOfTryAboutId(
                LogStudentInProblemID.ofExcludeCountOfTry(contestId, studentId, problemId, contentId));

        // then
        assertThat(maxCount).isEqualTo(count);
    }

    @DisplayName("문제를 푼 기록 중에 최고 점수 반환|Success|최고 점수 반환")
    @ValueSource(doubles = {0.8, 0.7, 0.6, 0.5})
    @ParameterizedTest(name = "최고 점수: {0}")
    void givenMaxScoreDesc0_01_whenGetMAxScoreProblemOne_thenReturnMaxScore(double maxScore) {
        // given
        UUID contestId = UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f");
        UUID studentId = UUID.fromString("d7762394-592c-4e33-8d71-06fc5a94abfb");
        Long problemId = 1L;
        Long contentId = 0L;
        for (int i = 0; i < 3; i++) {
            logStudentInProblemRepository.saveAndFlush(new LogStudentInProblem(
                    LogStudentInProblemID.ofIncludeCountOfTry(
                            contestId, studentId, problemId, contentId, i + 1)
                    , maxScore - i * 0.01));
        }

        // when
        Double actual = logStudentInProblemRepository.getMaxScoreProblemOne(
                LogStudentInProblemID.ofExcludeCountOfTry(contestId, studentId, problemId, contentId));

        // then
        assertThat(actual).isEqualTo(maxScore);
    }

    @DisplayName("기록이 없는 상태에서 max값 조회|Success|0을 반환")
    @Test
    void givenHasNotLog_whenGetMaxScoreProblemOne_thenReturnZero() {
        // given
        UUID contestId = UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f");
        UUID studentId = UUID.fromString("d7762394-592c-4e33-8d71-06fc5a94abfb");
        Long problemId = 1L;
        Long contentId = 0L;
        // when
        Double actual = logStudentInProblemRepository.getMaxScoreProblemOne(
                LogStudentInProblemID.ofExcludeCountOfTry(contestId, studentId, problemId, contentId));

        // then
        Double expect = 0.0;
        assertThat(actual).isEqualTo(expect);
    }

}