package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DataJpaTest
class ContestRepositoryTest {

    @Autowired
    private ContestRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("신청한 대회 중 시작하지 않은 대회 조회|Success|2건의 대회가 존재")
    @Test
    void givenStudentIdWhenGetRegisteredContestsInFromToThenListSizeOf2() {
        UUID studentId = UUID.fromString("d7762394-592c-4e33-8d71-06fc5a94abfb");
        LocalDateTime from = LocalDateTime.now().minusMinutes(1);
        LocalDateTime to = from.plusMonths(3);
        List<RegisteredContestDto> actual = repository.getRegisteredContestsInFromTo(studentId, from, to);
        assertThat(actual)
                .isNotNull()
                .hasSize(2)
                .extracting("id")
                // contain all element
                .containsExactlyInAnyOrderElementsOf(List.of(
                        UUID.fromString("992033a0-11c9-45b0-a643-01a2c706f118"),
                        UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f")));

    }

    @DisplayName("신청 취소 요청|Success| SoftDelete 성공")
    @Test
    void givenRegisteredContestWhenCancelContestThenSuccess() {
        // given
        UUID studentId = UUID.fromString("dba77299-9009-422c-91ab-7a976525c80a");
        UUID contestId = UUID.fromString("a3030109-b69e-417a-8b18-e2d12a3c33de");

        // when
        repository.cancelContest(contestId, studentId);

        // then
        StudentInContest dao = (StudentInContest) testEntityManager.getEntityManager()
                .createNativeQuery("select * from student_in_contest " +
                        "where student_id = :studentId and  contest_id = :contestId", StudentInContest.class)
                .setParameter("studentId", studentId).setParameter("contestId", contestId)
                .getSingleResult();
        assertThat(dao).isNotNull().extracting("deletedAt").isNotNull();
    }

    @DisplayName("대회 포기 요청|Success| isResigned 플래그가 true로 변경")
    @Test
    void givenStudentIdAndContestIdWhenResignedContestThenIsResignedIsTrue() {
        // given
        UUID studentId = UUID.fromString("d7762394-592c-4e33-8d71-06fc5a94abfb");
        UUID contestId = UUID.fromString("992033a0-11c9-45b0-a643-01a2c706f118");

        // when
        repository.resignContest(studentId, contestId);

        // then
        StudentInContest entity = testEntityManager.find(StudentInContest.class, new StudentInContestID(studentId, contestId));
        assertThat(entity).isNotNull().extracting("isResigned").isEqualTo(true);
    }

    @DisplayName("종료된 대회 요청|Success|종료된 대회들만 조회")
    @Test
    void givenStudentIdWhenSearchEndOfContestsThen() {
        // given
        UUID studentId = UUID.fromString("d7762394-592c-4e33-8d71-06fc5a94abfb");
        LocalDateTime from = LocalDateTime.now().minusMonths(3);
        LocalDateTime to = LocalDateTime.now().minusMinutes(1);
        // when
        List<ContestDto> endOfContests = repository.searchEndOfContests(studentId, from, to);

        // then
        UUID endContestId = UUID.fromString("d45fa47f-b1de-42b2-9b59-82b6cacb1614");
        assertThat(endOfContests).isNotNull().hasSize(1)
                .extracting("contestId").containsExactly(endContestId);
    }
}