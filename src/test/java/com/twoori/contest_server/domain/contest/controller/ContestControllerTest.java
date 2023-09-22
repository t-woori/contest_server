package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDtoForController;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.ContestVO;
import com.twoori.contest_server.domain.contest.vo.ContestsVO;
import com.twoori.contest_server.domain.contest.vo.SearchContestVO;
import com.twoori.contest_server.domain.contest.vo.SearchContestsVO;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;


@ExtendWith({MockitoExtension.class})
class ContestControllerTest {

    @InjectMocks
    private ContestController contestController;

    @Mock
    private ContestService contestService;

    @Mock
    private SecurityUtil securityUtil;

    private static Stream<Arguments> argumentsForSearchContest() {
        return Stream.of(
                Arguments.of(0, 100),
                Arguments.of(1, 100),
                Arguments.of(10, 100),
                Arguments.of(100, 100)
        );
    }

    @DisplayName("대회 검색|Success|검색 결과 totalContestCount 중 registeredContestCount 건이 신청한 대회")
    @MethodSource("argumentsForSearchContest")
    @ParameterizedTest
    void givenSearchParameterWhenSearchContestsThenTotalContestCountOfContestInRegisteredContestCountOfContest(
            int registeredContestCount, int totalContestCount
    ) {
        // given
        String parameter = "";
        List<UUID> contestIds = IntStream.range(0, totalContestCount).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusMonths(3);
        UUID studentId = UUID.randomUUID();
        String mockHeader = "";
        given(securityUtil.validateAuthorization(mockHeader)).willReturn(new StudentDto(studentId,
                "name",
                "email",
                "phoneNumber",
                "accessToken",
                "refreshToken"));
        given(contestService.searchContests(parameter, from, to)).willReturn(
                IntStream.range(0, totalContestCount).mapToObj(i -> new SearchContestDtoForController(contestIds.get(i),
                        "contest name" + i,
                        "host" + i,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(10))).toList());
        given(contestService.getRegisteredContestIdsInFromTo(eq(studentId), isA(LocalDate.class), isA(LocalDate.class))).willReturn(
                contestIds.stream().limit(registeredContestCount).collect(Collectors.toSet())
        );

        // when
        ResponseEntity<SearchContestsVO> actual = contestController.searchContests(mockHeader, parameter, from, to);

        // then
        List<SearchContestVO> body = actual.getBody().getSearchContestVOList();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body)
                .isNotNull()
                .hasSize(totalContestCount)
                .isSortedAccordingTo(
                        Comparator.comparing(SearchContestVO::startAt)
                                .thenComparing(SearchContestVO::endAt));
        assertThat(body)
                .filteredOn(SearchContestVO::isRegistered)
                .hasSize(registeredContestCount);
    }

    @DisplayName("신청한 대회중 시작하지 않은 대회 조회|Success|검색 결과 20건이 반환")
    @Test
    void givenNonParameterWhenGetRegisteredContestThenList20() {
        // given
        List<UUID> contestIds = IntStream.range(0, 20).mapToObj(i -> UUID.randomUUID()).toList();
        UUID studentId = UUID.randomUUID();
        String mockHeader = "";
        given(securityUtil.validateAuthorization(mockHeader)).willReturn(new StudentDto(studentId,
                "name",
                "email",
                "phoneNumber",
                "accessToken",
                "refreshToken"));
        given(contestService.getRegisteredContestsInFromTo(studentId)).willReturn(
                IntStream.range(0, 20).mapToObj(i -> new ContestDto(contestIds.get(i),
                        "contest name" + i,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(10))).toList());

        // when
        ResponseEntity<ContestsVO> actual = contestController.getRegisteredContestsAboutStudent(mockHeader);

        // then
        List<ContestVO> body = actual.getBody().getContests();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body)
                .isNotNull()
                .hasSize(20)
                .isSortedAccordingTo(
                        Comparator.comparing(ContestVO::startAt)
                                .thenComparing(ContestVO::endAt));
    }
}