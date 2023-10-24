package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.student.dao.Student;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DataJpaTest
class StudentInContestRepositoryTest {

    private static final int CONTEST_TIME = 15;
    @Autowired
    private StudentInContestRepository studentInContestRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private final Student student = Student.builder()
            .id(UUID.randomUUID()).phoneNumber("phoneNumber")
            .email("email").accessToken("accessToken").refreshToken("refreshToken").name("name").build();

    @BeforeEach
    void setUpContestAndStudent() {
        testEntityManager.persist(student);
    }

    @DisplayName("진입 가능한 대회 조회|Success|진입 가능한 대회가 존재")
    @Test
    void givenAccessibleContestWhenFindById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeThenReturnStudentInContestThenReturnEntityOne() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = new Contest(UUID.randomUUID(), "test", "test", "test",
                now.minusMinutes(2), now.plusMinutes(CONTEST_TIME), 0.5, 0.5);
        StudentInContest studentInContest = StudentInContest.builder()
                .id(new StudentInContestID(student.getId(), contest.getId()))
                .isEntered(true).isResigned(false)
                .build();
        testEntityManager.persist(contest);
        testEntityManager.persist(studentInContest);

        // when
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isNotEmpty()
                .isEqualTo(Optional.of(studentInContest));
    }

    @DisplayName("진입 가능한 대회 조회|Fail|포기한 대회")
    @Test
    void givenResignContestWhenFindById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeThenReturnStudentInContestThenReturnEmpty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = new Contest(UUID.randomUUID(), "test", "test", "test",
                now.minusMinutes(2), now.plusMinutes(CONTEST_TIME), 0.5, 0.5);
        testEntityManager.persist(contest);
        testEntityManager.persist(StudentInContest.builder()
                .id(new StudentInContestID(student.getId(), contest.getId()))
                .isEntered(true).isResigned(true)
                .build());

        // when
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isEmpty();
    }

    @DisplayName("진입 가능한 대회 조회|Fail|종료된 대회")
    @Test
    void givenExpiredContestWhenFindById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeThenReturnStudentInContestThenReturnEmpty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = new Contest(UUID.randomUUID(), "test", "test", "test",
                now.minusMinutes(CONTEST_TIME * 2), now.minusMinutes(CONTEST_TIME),
                0.5, 0.5);
        testEntityManager.persist(contest);
        testEntityManager.persist(StudentInContest.builder()
                .id(new StudentInContestID(student.getId(), contest.getId()))
                .isEntered(true).isResigned(false)
                .build());

        // when
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isEmpty();
    }

    @DisplayName("진입 가능한 대회 조회|Fail|진입하지 않은 대회")
    @Transactional
    @Test
    void givenNotEnteredContestWhenFindById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeThenReturnStudentInContestThenReturnEmpty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = new Contest(UUID.randomUUID(), "test", "test", "test",
                now.minusMinutes(2), now.plusMinutes(CONTEST_TIME), 0.5, 0.5);
        testEntityManager.persist(contest);
        testEntityManager.persist(StudentInContest.builder()
                .id(new StudentInContestID(student.getId(), contest.getId()))
                .isEntered(false).isResigned(false)
                .build());

        // when
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isEmpty();
    }
}