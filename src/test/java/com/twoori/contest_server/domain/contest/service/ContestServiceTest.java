package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dto.*;
import com.twoori.contest_server.domain.contest.excpetion.*;
import com.twoori.contest_server.domain.contest.mapper.ContestDtoForControllerMapper;
import com.twoori.contest_server.domain.contest.mapper.ContestDtoForControllerMapperImpl;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
import com.twoori.contest_server.domain.student.dao.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
    @Spy
    private ContestDtoForControllerMapper mapper = new ContestDtoForControllerMapperImpl();

    @DisplayName("Success case1: 대회 시작 10분전에 입장 시도")
    @Test
    void givenContestWhenEnterStudentInContestThenSuccess() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.minusMinutes(ENTER_TIME);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));

        // when
        EnterContestDtoForController actual = contestService.enterStudentInContest(student.getId(), contestId, enterDateTime);

        // then
        assertThat(actual)
                .extracting("id", "runningStartDateTime", "runningEndDateTime")
                .doesNotContainNull()
                .containsExactly(contestId, startDateTime, endDateTime);
        verify(contestRepository).updateEnterStudentInContest(student.getId(), contestId);

    }

    @DisplayName("Success case2: 대회 기간 내에 재진입 허용")
    @Test
    void givenReEnterStatusWhenEnterStudentInContestThenSuccess() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.plusMinutes(ENTER_TIME);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));
        given(contestRepository.isResigned(contestId, student.getId())).willReturn(false);
        given(contestRepository.isEnteredStudentInContest(student.getId(), contestId)).willReturn(true);

        // when
        EnterContestDtoForController actual = contestService.enterStudentInContest(student.getId(), contestId, enterDateTime);

        // then
        assertThat(actual)
                .extracting("id", "runningStartDateTime", "runningEndDateTime")
                .doesNotContainNull()
                .containsExactly(contestId, startDateTime, endDateTime);
        verify(contestRepository).updateEnterStudentInContest(student.getId(), contestId);

    }

    @DisplayName("Success case3: 대회 시작 1분후에 입장 시도")
    @Test
    void givenEnterContestWhenEnterStudentInContestThenSuccess() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.plusMinutes(1);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
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
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));
        // when & then
        assertThatThrownBy(() -> contestService.enterStudentInContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(EndContestException.class);
    }

    @DisplayName("Fail case2: 대회 대기 시간 전에 입장 시도")
    @Test
    void givenNotStartContestWhenEnterStudentInContestThenThrowEarlyContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.minusMinutes(ENTER_TIME + 1);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
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
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));
        given(contestRepository.isResigned(contestId, student.getId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> contestService.enterStudentInContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(ResignedContestException.class);
    }

    @DisplayName("Fail case4: 존재 하지 않는 대회 진 ")
    @Test
    void givenHasNotContestWhenEnterStudentInContestThenThrowResignedContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime enterDateTime = LocalDateTime.now();
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> contestService.enterStudentInContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(NotFoundContestException.class);
    }

    @DisplayName("Fail Case 5: 대회에 최초 진입을 2분뒤에 진행")
    @Test
    void givenReEnterContestWhenReEnterStudentInContestThenSuccess() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.plusMinutes(3);

        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));
        given(contestRepository.isResigned(contestId, student.getId())).willReturn(false);
        given(contestRepository.isEnteredStudentInContest(student.getId(), contestId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> contestService.enterStudentInContest(student.getId(), contestId, enterDateTime))
                .isInstanceOf(ExpiredTimeException.class);
    }

    @DisplayName("대회 검색|Success|현시점부터 3개월간의 대회 데이터를 모두 조회")
    @Test
    void givenNonParamWhenSearchContestThenGetContestsIn3Month() {
        // given
        String parameter = "";
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(3);
        given(contestRepository.getContestsHasParameterInName(eq(parameter),
                isA(LocalDateTime.class),
                isA(LocalDateTime.class))).willReturn(
                IntStream.range(0, 100).mapToObj(i -> new SearchContestDto(
                                contestIds.get(i),
                                "test" + i,
                                "hostName" + i,
                                from.atStartOfDay().plusDays(1),
                                from.atStartOfDay().plusDays(2)
                        )
                ).toList()
        );

        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        assertThat(contests)
                .doesNotContainNull().isNotEmpty().hasSize(100)
                .isSortedAccordingTo(Comparator.comparing(SearchContestDtoForController::startedAt)
                        .thenComparing(SearchContestDtoForController::endedAt))
                .extracting("id").containsExactlyElementsOf(contestIds);

    }


    @DisplayName("대회 검색|Success|현시점부터 대회 이름으로 1개월간 데이터 검색")
    @Test
    void givenDueTo1MonthWhenSearchContestsThen70OfSortedContestIn100Contests() {
        // given
        String parameter = "search_param";
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(1);
        given(contestRepository.getContestsHasParameterInName(eq(parameter),
                isA(LocalDateTime.class),
                isA(LocalDateTime.class))).willReturn(
                IntStream.range(0, 100).mapToObj(i -> new SearchContestDto(
                        contestIds.get(i),
                        "contest" + i,
                        "hostName" + i,
                        from.atStartOfDay().plusDays(1),
                        from.atStartOfDay().plusDays(2)
                )).toList()
        );
        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        assertThat(contests)
                .isNotNull().doesNotContainNull().isNotEmpty().hasSize(100)
                .isSortedAccordingTo(Comparator.comparing(SearchContestDtoForController::startedAt)
                        .thenComparing(SearchContestDtoForController::endedAt))
                .extracting("id").containsExactlyElementsOf(contestIds);
    }

    @DisplayName("대회 검색|Success|현시점부터 1개월 이상, 2개월 미만인 모든 대회 검색")
    @Test
    void givenDueTo1To2MonthWhenSearchContestsThen60OfSortedContests() {
        //given
        String parameter = "";
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now().plusMonths(1);
        LocalDate to = from.plusMonths(2);
        given(contestRepository.getContestsHasParameterInName(eq(parameter),
                isA(LocalDateTime.class),
                isA(LocalDateTime.class))).willReturn(
                IntStream.range(0, 100).mapToObj(i -> {
                            LocalDateTime startedAt = LocalDateTime.now().plusDays(1);
                    return new SearchContestDto(
                                    contestIds.get(i),
                                    "contest" + i,
                                    "hostName" + i,
                                    startedAt,
                                    startedAt.plusMinutes(CONTEST_TIME)
                            );
                        }
                ).toList()
        );
        //when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        //then
        assertThat(contests)
                .isNotNull().doesNotContainNull().isNotEmpty().hasSize(100)
                .isSortedAccordingTo(Comparator.comparing(SearchContestDtoForController::startedAt)
                        .thenComparing(SearchContestDtoForController::endedAt))
                .extracting("id").containsExactlyElementsOf(contestIds);

    }

    @DisplayName("대회 검색|Success|from이 to보다 큰 검색")
    @Test
    void givenFromAfterToWhenSearchContestsThenListSizeOfZero() {
        // given
        String parameter = "";
        LocalDate from = LocalDate.now().plusMonths(1);
        LocalDate to = from.minusMonths(1);

        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        verify(contestRepository, never()).getContestsHasParameterInName(anyString(), any(), any());
        assertThat(contests).isNotNull().isEmpty();
    }

    @DisplayName("대회 검색|Success|from이 현재보다 작은 검색")
    @Test
    void givenFromBeforeNowWhenSearchContestsThenListSizeOfZero() {
        // given
        String parameter = "";
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = from.plusMonths(1);

        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        verify(contestRepository, never()).getContestsHasParameterInName(anyString(), any(), any());
        assertThat(contests).isNotNull().isEmpty();
    }

    @DisplayName("신청한 대회 중 시작하지 않은 대회 조회|Success|대회 검색 결과가 존재")
    @Test
    void givenStudentIdWhenGetRegisteredContestsThenSuccess() {
        // given
        UUID studentId = UUID.randomUUID();
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        given(contestRepository.getRegisteredContestsInFromTo(eq(studentId), isA(LocalDateTime.class), isA(LocalDateTime.class)))
                .willReturn(IntStream.range(0, 100).mapToObj(i -> new RegisteredContestDto(contestIds.get(i),
                        "contest" + i,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2))).toList());

        // when
        List<RegisteredContestDto> actual = contestService.getRegisteredContestsInFromTo(studentId);

        // then
        assertThat(actual)
                .isNotNull().isNotEmpty().hasSize(100)
                .isSortedAccordingTo(Comparator.comparing(RegisteredContestDto::startedAt)
                        .thenComparing(RegisteredContestDto::endedAt))
                .extracting("id").containsExactlyElementsOf(contestIds);
    }

    @DisplayName("신청한 대회 중 시작하지 않은 대회 조회|Success|대회 검색 결과 미존재")
    @Test
    void givenStudentIdWhenGetRegisteredContestsThenEmptyList() {
        // given
        UUID studentId = UUID.randomUUID();
        given(contestRepository.getRegisteredContestsInFromTo(eq(studentId), isA(LocalDateTime.class), isA(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        List<RegisteredContestDto> actual = contestService.getRegisteredContestsInFromTo(studentId);

        // then
        assertThat(actual)
                .isNotNull().isEmpty();
    }
}