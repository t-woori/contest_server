package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.student.dao.Student;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.global.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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
    private StudentInContestRepository studentInContestRepository;

    @DisplayName("Fail case1: 대회가 종료된 후 입장 시도")
    @Test
    void givenEndContestWhenGetAccessibleContestInformationThenThrowExpiredContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = endDateTime.plusMinutes(1);
        Contest endContest = Contest.builder()
                .id(contestId)
                .runningStartDateTime(startDateTime)
                .runningEndDateTime(endDateTime)
                .build();
        StudentInContest studentInContest = StudentInContest.builder().student(student).contest(endContest).build();
        given(studentInContestRepository.findByContest_IdAndStudent_Id(contestId, student.getId())).willReturn(Optional.of(studentInContest));
        // when & then
        assertThatThrownBy(() -> contestService.getAccessibleContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("Fail case2: 대회 대기 시간 전에 입장 시도")
    @Test
    void givenEarlyContestWhenGetAccessibleContestInformationThenThrowEarlyContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.minusMinutes(ENTER_TIME + 1);
        Contest earlyContest = Contest.builder()
                .id(contestId)
                .runningStartDateTime(startDateTime)
                .runningEndDateTime(endDateTime)
                .build();
        StudentInContest studentInContest = StudentInContest.builder().student(student).contest(earlyContest).build();
        // when & then
        when(studentInContestRepository.findByContest_IdAndStudent_Id(contestId, student.getId())).thenReturn(Optional.of(studentInContest));
        assertThatThrownBy(() -> contestService.getAccessibleContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("Success case: 대회 시작 10분전에 입장 시도")
    @Test
    void givenContestWhenGetAccessibleContestInformationThenContestDTO() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.minusMinutes(ENTER_TIME);
        Contest contest = Contest.builder()
                .id(contestId)
                .runningStartDateTime(startDateTime)
                .runningEndDateTime(endDateTime)
                .build();
        StudentInContest studentInContest = StudentInContest.builder().student(student).contest(contest).build();
        given(studentInContestRepository.findByContest_IdAndStudent_Id(contestId, student.getId())).willReturn(Optional.of(studentInContest));

        // when
        ContestDto contestDTO = contestService.getAccessibleContest(student.getId(), contestId, enterDateTime);

        // then
        assertThat(contestDTO)
                .extracting("id", "runningStartDateTime", "runningEndDateTime")
                .doesNotContainNull()
                .containsExactly(contestId, startDateTime, endDateTime);
    }
}