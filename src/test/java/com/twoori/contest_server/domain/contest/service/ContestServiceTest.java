package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.excpetion.EarlyEnterTimeException;
import com.twoori.contest_server.domain.contest.excpetion.ExpiredTimeException;
import com.twoori.contest_server.domain.contest.excpetion.ResignedContestException;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
import com.twoori.contest_server.domain.student.dao.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
class ContestServiceTest {
    public static final int ENTER_TIME = 10;
    public static final int CONTEST_TIME = 15;
    @InjectMocks
    private ContestService contestService;
    private final Student student = Student.builder()
            .id(UUID.randomUUID())
            .build();

    @Mock
    private ContestRepository contestRepository;

    @DisplayName("Success case1: 대회 시작 10분전에 입장 시도")
    @Test
    void givenContestWhenEnterStudentInContestThenSuccess() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.minusMinutes(ENTER_TIME);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(new EnterContestDto(
                contestId,
                "name",
                "hostName",
                startDateTime,
                endDateTime
        ));

        // when
        EnterContestDtoForController actual = contestService.enterStudentInContest(student.getId(), contestId, enterDateTime);

        // then
        assertThat(actual)
                .extracting("id", "runningStartDateTime", "runningEndDateTime")
                .doesNotContainNull()
                .containsExactly(contestId, startDateTime, endDateTime);
    }

    @DisplayName("Success case2: 대회 기간 내에 재진입 허용")
    @Test
    void givenReEnterStatusWhenEnterStudentInContestThenSuccess() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.plusMinutes(ENTER_TIME);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(new EnterContestDto(
                contestId,
                "name",
                "hostName",
                startDateTime,
                endDateTime
        ));
        given(contestRepository.isResigned(contestId, student.getId())).willReturn(false);

        // when
        EnterContestDtoForController actual = contestService.enterStudentInContest(student.getId(), contestId, enterDateTime);

        // then
        assertThat(actual)
                .extracting("id", "runningStartDateTime", "runningEndDateTime")
                .doesNotContainNull()
                .containsExactly(contestId, startDateTime, endDateTime);

    }

    @DisplayName("Success case3: 대회 시작 1분후에 입장 시도")
    @Test
    void givenEnterContestWhenEnterStudentInContestThenSuccess() {
        // given
        UUID studentId = UUID.randomUUID();
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.plusMinutes(1);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(new EnterContestDto(
                contestId,
                "name",
                "hostName",
                startDateTime,
                endDateTime
        ));
        given(contestRepository.isResigned(contestId, student.getId())).willReturn(false);

        // when
        EnterContestDtoForController actual = contestService.enterStudentInContest(student.getId(), contestId, enterDateTime);

        // then
        assertThat(actual)
                .extracting("id", "runningStartDateTime", "runningEndDateTime")
                .doesNotContainNull()
                .containsExactly(contestId, startDateTime, endDateTime);

    }

    @DisplayName("Fail case1: 대회가 종료된 후 입장 시도")
    @Test
    void givenEndContestWhenEnterStudentInContestThenThrowExpiredContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = endDateTime.plusMinutes(1);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(new EnterContestDto(
                contestId,
                "name",
                "hostName",
                startDateTime,
                endDateTime
        ));
        // when & then
        assertThatThrownBy(() -> contestService.enterStudentInContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(ExpiredTimeException.class);
    }

    @DisplayName("Fail case2: 대회 대기 시간 전에 입장 시도")
    @Test
    void givenNotStartContestWhenEnterStudentInContestThenThrowEarlyContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.minusMinutes(ENTER_TIME + 1);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(new EnterContestDto(
                contestId,
                "name",
                "hostName",
                startDateTime,
                endDateTime
        ));
        // when & then
        assertThatThrownBy(() -> contestService.enterStudentInContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(EarlyEnterTimeException.class);
    }

    @DisplayName("Fail case3: 대회 자진 포기 후 재입장 시도")
    @Test
    void givenResignedContestWhenEnterStudentInContestThenThrowResignedContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.plusMinutes(3);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(new EnterContestDto(
                contestId,
                "name",
                "hostName",
                startDateTime,
                endDateTime
        ));
        given(contestRepository.isResigned(contestId, student.getId())).willReturn(true);
        // when & then
        assertThatThrownBy(() -> contestService.enterStudentInContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(ResignedContestException.class);
    }

}