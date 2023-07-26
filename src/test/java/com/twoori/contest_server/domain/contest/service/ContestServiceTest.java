package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class ContestServiceTest {
    public static final int ENTER_TIME = 10;
    public static final int CONTEST_TIME = 15;
    @InjectMocks
    private ContestService contestService;
    @Mock
    private ContestRepository contestRepository;


    @DisplayName("Fail case1: 대회가 종료된 후 입장 시도")
    @Test
    void givenLateTimeWhenIsEnterContestThenFalse() {
        LocalDateTime now = LocalDateTime.now();
        // given
        LocalDateTime lateDateTime = now.plusMinutes(CONTEST_TIME + 1);
        // when
        UUID contestId = UUID.randomUUID();
        when(contestRepository.findById(contestId)).thenReturn(Optional.of(Contest.builder()
                .id(contestId)
                .runningStartDateTime(now)
                .runningEndDateTime(now.plusMinutes(CONTEST_TIME))
                .build()));
        boolean result = contestService.isEnterContest(contestId, lateDateTime);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("Fail case2: 대회 대기 시간 전에 입장 시도")
    @Test
    void givenEarlyTimeWhenIsEnterContestThenFalse() {
        LocalDateTime now = LocalDateTime.now();
        // given
        LocalDateTime earlyTime = now.minusMinutes(ENTER_TIME + 1);
        // when
        UUID contestId = UUID.randomUUID();
        when(contestRepository.findById(contestId)).thenReturn(Optional.of(Contest.builder()
                .id(contestId)
                .runningStartDateTime(now)
                .runningEndDateTime(now.plusMinutes(CONTEST_TIME))
                .build()));
        boolean result = contestService.isEnterContest(contestId, earlyTime);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("Success case: 대회 시작 10분전에 입장 시도")
    @Test
    void givenEnterTimeWhenIsEnterContestThenTrue() {
        LocalDateTime now = LocalDateTime.now();
        // given
        LocalDateTime enterTime = now.minusMinutes(ENTER_TIME);

        // when
        UUID contestId = UUID.randomUUID();
        when(contestRepository.findById(contestId)).thenReturn(Optional.of(Contest.builder()
                .id(contestId)
                .runningStartDateTime(now)
                .runningEndDateTime(now.plusMinutes(CONTEST_TIME))
                .build()));
        boolean result = contestService.isEnterContest(contestId, enterTime);

        //then
        assertThat(result).isTrue();
    }
}