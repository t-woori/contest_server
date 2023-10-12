package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.ProblemInContest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@DataJpaTest
class ProblemInContestRepositoryTest {

    @Autowired
    private ProblemInContestRepository problemInContestRepository;

    @DisplayName("대회에 속한 모든 문제 번호 조회|Success|10건의 문제 조회 성공")
    @Test
    void givenContestIdyWhenFindById_ContestIdThenListOfReturnProblemInContest() {
        // given
        UUID contestId = UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f");

        // when
        List<ProblemInContest> actual = problemInContestRepository.findById_ContestId(contestId);

        // then
        assertThat(actual).hasSize(10);
    }

}