package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.student.dao.Student;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
            .id(UUID.randomUUID()).accessToken("accessToken").refreshToken("refreshToken").nickname("name").build();

    @BeforeEach
    void setUpContestAndStudent() {
        testEntityManager.persist(student);
    }

    @DisplayName("진입 가능한 대회 조회|Success|진입 가능한 대회가 존재")
    @Test
    void givenAccessibleContest_findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull_thenReturnStudentInContestThenReturnEntityOne() {
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
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isNotEmpty()
                .isEqualTo(Optional.of(studentInContest));
    }

    @DisplayName("진입 가능한 대회 조회|Fail|포기한 대회")
    @Test
    void givenResignContest_findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull_thenReturnStudentInContestThenReturnEmpty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = new Contest(UUID.randomUUID(), "test", "test", "test",
                now.minusMinutes(2), now.plusMinutes(CONTEST_TIME), 0.5, 0.5);
        testEntityManager.persistAndFlush(contest);
        testEntityManager.persist(StudentInContest.builder()
                .id(new StudentInContestID(student.getId(), contest.getId()))
                .isEntered(true).isResigned(true)
                .build());

        // when
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isEmpty();
    }

    @DisplayName("진입 가능한 대회 조회|Fail|시간이 초과된 대회")
    @Test
    void givenExpiredContest_findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull_thenReturnStudentInContestThenReturnEmpty() {
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
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isEmpty();
    }

    @DisplayName("진입 가능한 대회 조회|Fail|명시적으로 종료한 대회")
    @Test
    void givenEndContestEarly_findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull_thenReturnStudentInContestThenReturnEmpty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startContestTime = now.minusMinutes(2);
        LocalDateTime endContestTime = now.plusMinutes(CONTEST_TIME);
        Contest contest = new Contest(UUID.randomUUID(), "test", "test", "test",
                startContestTime, endContestTime,
                0.5, 0.5);
        testEntityManager.persist(contest);
        testEntityManager.persist(StudentInContest.builder()
                .id(new StudentInContestID(student.getId(), contest.getId()))
                .isEntered(true).isResigned(false)
                .endContestAt(endContestTime.minusMinutes(2))
                .build());

        // when
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull(
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
        Optional<StudentInContest> optionalEntity = studentInContestRepository.findById_StudentIDAndContest_RunningEndDateTimeAfterAndIsEnteredTrueAndIsResignedFalseAndEndContestAtNull(
                student.getId(), now);

        // then
        assertThat(optionalEntity).isEmpty();
    }

    @DisplayName("대회 참가자 수 조회|Success|참가자 수가 n명")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    @ParameterizedTest(name = "참가자수: {0}")
    void givenContestId_whenCountByID_ContestID_thenCountOfN(int n) {
        // given
        Contest contest = new Contest(
                UUID.randomUUID(), "test", "test", "test",
                LocalDateTime.now().minusMinutes(2), LocalDateTime.now().plusMinutes(CONTEST_TIME),
                0.5, 0.5
        );
        testEntityManager.persist(contest);
        Stream.generate(UUID::randomUUID)
                .limit(n)
                .peek(studentId -> {
                    Student mockStudent = Student.builder().id(studentId).nickname("nickname").build();
                    testEntityManager.persist(mockStudent);
                })
                .forEach(studentId -> {
                    StudentInContest studentInContest = StudentInContest.builder()
                            .id(new StudentInContestID(studentId, contest.getId()))
                            .isEntered(true).isResigned(false)
                            .build();
                    testEntityManager.persist(studentInContest);
                });


        // when
        long count = studentInContestRepository.countById_ContestID(contest.getId());

        // then
        assertThat(count).isEqualTo(n);
    }

    @DisplayName("대회 신청 기록 조회|Success|Soft delete 포함한 검색")
    @Test
    void givenIncludeSoftDelete_whenFindByIdIncludeSoftDelete_thenFind() {
        // given
        Contest contest = new Contest(
                UUID.randomUUID(), "test", "test", "test",
                LocalDateTime.now().minusMinutes(2), LocalDateTime.now().plusMinutes(CONTEST_TIME),
                0.5, 0.5
        );
        Student student = Student.builder().id(UUID.randomUUID()).nickname("nickname").build();
        StudentInContest studentInContest = StudentInContest.builder()
                .id(new StudentInContestID(student.getId(), contest.getId()))
                .contest(contest).student(student)
                .deletedAt(LocalDateTime.now())
                .isEntered(true).isResigned(false)
                .build();
        testEntityManager.persist(contest);
        testEntityManager.persist(student);
        testEntityManager.persist(studentInContest);

        // when
        Optional<StudentInContest> result = studentInContestRepository.findByIdIncludeSoftDelete(contest.getId(), student.getId());

        // then
        assertThat(result).isNotEmpty()
                .map(StudentInContest::getDeletedAt)
                .isNotEmpty();
    }
}