package com.twoori.contest_server.domain.student.service;

import com.twoori.contest_server.domain.problem.dto.ProblemIdDto;
import com.twoori.contest_server.domain.problem.dto.UpdateProblemCountDto;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import com.twoori.contest_server.global.config.TestRedisContainerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(TestRedisContainerConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class TrackingStudentServiceTest {

    @Autowired
    private RedisTemplate<String, Long> totalStatusRedisTemplate;

    @Autowired
    private RedisTemplate<StudentInContestIdDto, ProblemIdDto> studentRedisTemplate;

    @Autowired
    private TrackingStudentService trackingStudentService;

    private HashOperations<String, ProblemIdDto, Long> hashOp;

    @BeforeEach
    void setUpHashOp() {
        hashOp = totalStatusRedisTemplate.opsForHash();
    }

    @AfterEach
    void clearRedis() {
        Objects.requireNonNull(totalStatusRedisTemplate.getConnectionFactory())
                .getConnection().serverCommands().flushDb();
    }

    @DisplayName("학생이 푼 문제 기록이 없다|Success|첫번째 문제로 초기화하고 상태를 모니터링에 첫번째 문제를 기록")
    @Test
    void givenStudentHasNoProblemRecord_whenUpdateProblemCountAboutStudent_thenInitializeStudentStatus() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StudentInContestIdDto givenStudentInContestIdDto = new StudentInContestIdDto(contestId, studentId);
        ProblemIdDto givenProblemIdDto = new ProblemIdDto(0L, 0L);
        UpdateProblemCountDto givenDto = new UpdateProblemCountDto(givenStudentInContestIdDto, givenProblemIdDto);

        // when
        trackingStudentService.updateProblemCountAboutStudent(givenDto);

        // then
        ProblemIdDto savedDto = studentRedisTemplate.opsForValue().get(givenDto.studentInContestIdDto());
        assertThat(savedDto).isEqualTo(givenProblemIdDto);
        assertThat(hashOp.get("student_count", givenDto.problemIdDto())).isEqualTo(1);

    }

    @DisplayName("n번째 문제를 k번 품|Success|count횟수는 변화가 없다")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @ParameterizedTest(name = "시도 횟수: {0}")
    void givenStudentHasProblemRecord_whenUpdateProblemCountAboutStudent_thenUpdateTryCount(int tryCount) {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UpdateProblemCountDto dto = new UpdateProblemCountDto(new StudentInContestIdDto(contestId, studentId),
                new ProblemIdDto(0L, 0L));

        // when
        for (int i = 0; i < tryCount; i++) {
            trackingStudentService.updateProblemCountAboutStudent(dto);
        }

        // then
        assertThat(hashOp.get("student_count", dto.problemIdDto())).isEqualTo(1);

    }

    @DisplayName("대회 종료 시그널을 보냄|Success|문제 모니터링에서 개수가 차감")
    @Test
    void givenStudentHasProblemRecord_whenUpdateProblemCountAboutStudent_thenDecreaseProblemCount() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        totalStatusRedisTemplate.opsForHash().increment("student_count", new ProblemIdDto(9, 0), 1L);
        studentRedisTemplate.opsForValue().setIfAbsent(new StudentInContestIdDto(contestId, studentId),
                new ProblemIdDto(9, 0));
        // when
        trackingStudentService.quitContest(new StudentInContestIdDto(contestId, studentId));

        // then
        Long studentCount = hashOp.get("student_count", new ProblemIdDto(9L, 0L));
        assertThat(studentCount).isZero();
    }

    @DisplayName("1번째 문제를 푼 기록이 있는 상태에서 2번째 문제를 요청|Success|n-1번째 문제를 푼 기록을 삭제하고 n번째 문제를 푼 기록을 추가")
    @Test
    void givenStudentHasProblemRecord_whenUpdateProblemCountAboutStudent_thenUpdateProblemRecord() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UpdateProblemCountDto dto = new UpdateProblemCountDto(new StudentInContestIdDto(contestId, studentId),
                new ProblemIdDto(0L, 0L));
        hashOp.put("student_count", dto.problemIdDto(), 1L);
        studentRedisTemplate.opsForValue().setIfAbsent(dto.studentInContestIdDto(), dto.problemIdDto());

        // when
        trackingStudentService.updateProblemCountAboutStudent(new UpdateProblemCountDto(dto.studentInContestIdDto(),
                new ProblemIdDto(1L, 0L)));

        // then
        assertThat(hashOp.get("student_count", dto.problemIdDto())).isZero();
        assertThat(hashOp.get("student_count", new ProblemIdDto(1L, 0L))).isEqualTo(1);
    }

    @DisplayName("10명의 학생들이 순서대로 최대 n번째까지 문제를 품|Success|대회 상태는 n까지는 1이고 나머지는 0, 학생상태는 n번째학생까지는 n의 문제를 풀고 있고 나머지 학생들은 0")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    @ParameterizedTest(name = "문제번호: {0}")
    void givenFistStudentProblemId_whenGetTotalStatus_thenReturnSizeOf10(int lastProblemId) {
        // given
        int maxStudentCount = 10;
        UUID contestId = UUID.randomUUID();
        List<UUID> studentIds = Stream.generate(UUID::randomUUID).limit(maxStudentCount).toList();
        for (int i = 0; i <= lastProblemId; i++) {
            UUID studentId = studentIds.get(i);
            for (int j = 0; j <= i; j++) {
                trackingStudentService.updateProblemCountAboutStudent(
                        new UpdateProblemCountDto(new StudentInContestIdDto(contestId, studentId),
                                new ProblemIdDto(j, 0L)));
            }
        }

        // when
        List<Long> result = trackingStudentService.getTotalStatus();

        // then
        assertThat(result).hasSize(maxStudentCount)
                .isEqualTo(IntStream.range(0, maxStudentCount).mapToObj(v -> {
                    if (v <= lastProblemId) {
                        return 1L;
                    }
                    return 0L;
                }).toList());
        for (int i = 0; i < maxStudentCount; i++) {
            ProblemIdDto expectProblemIdDto = new ProblemIdDto(i, 0L);
            if (i > lastProblemId) {
                expectProblemIdDto = new ProblemIdDto(0L, 0L);
            }
            assertThat(trackingStudentService.getStudentStatusInContest(new StudentInContestIdDto(contestId, studentIds.get(i))))
                    .describedAs("studentCount: %d, studentId: %s", i, studentIds.get(i))
                    .isEqualTo(expectProblemIdDto);
        }
    }

    @DisplayName("학생이 대회에서 가장 최근에 푼 문제 조회|Success|문제 기록이 존재")
    @Test
    void givenStudentInContestIdDto_whenGetStudentStatusInContest_thenReturnProblemIdDto() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StudentInContestIdDto studentInContestIdDto = new StudentInContestIdDto(studentId, contestId);
        ProblemIdDto problemIdDto = new ProblemIdDto(2, 0);
        studentRedisTemplate.opsForValue().setIfAbsent(studentInContestIdDto, problemIdDto);

        // when
        ProblemIdDto actual = trackingStudentService.getStudentStatusInContest(studentInContestIdDto);

        // then
        assertThat(actual).isEqualTo(problemIdDto);
    }

    @DisplayName("학생이 대회에서 가장 최근에 푼 문제 조회|Fail|문제 기록이 없어 첫번째 문제를 반환")
    @Test
    void givenStudentInContestIdDto_whenGetStudentStatusInContest_thenReturnFirstId() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StudentInContestIdDto studentInContestIdDto = new StudentInContestIdDto(studentId, contestId);

        // when
        ProblemIdDto actual = trackingStudentService.getStudentStatusInContest(studentInContestIdDto);

        // then
        assertThat(actual).isEqualTo(new ProblemIdDto(0, 0));
    }
}
