package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.student.dao.Student;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
@DataJpaTest
class StudentInContestRepositoryTest {

    private static final int CONTEST_TIME = 15;
    @Autowired
    private StudentInContestRepository studentInContestRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Transactional
    @Test
    void givenStudentIdWhenFindById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeThenReturnStudentInContestThenReturnEntityOne() {
        // given
        UUID contestId = UUID.randomUUID();
        Contest existsContestEntity = new Contest(
                contestId, "test", "test", "test",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(CONTEST_TIME),
                0.5, 0.5);
        Student existsStudentEntity = Student.builder()
                .id(UUID.randomUUID()).phoneNumber("phoneNumber").build();
        testEntityManager.persist(existsStudentEntity);
        testEntityManager.persist(existsContestEntity);
        testEntityManager.persist(StudentInContest.builder()
                .id(new StudentInContestID(existsStudentEntity.getId(), existsContestEntity.getId()))
                .isEntered(true).isResigned(false)
                .build());
        // when
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTime(
                existsStudentEntity.getId(), existsContestEntity.getRunningEndDateTime());

        // then
        assertThat(optionalEntity).isNotEmpty()
                .map(StudentInContest::getContest)
                .isEqualTo(existsContestEntity);
    }
}