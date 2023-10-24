package com.twoori.contest_server.domain.contest.service;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.*;
import com.twoori.contest_server.domain.contest.excpetion.*;
import com.twoori.contest_server.domain.contest.mapper.ContestDtoForControllerMapper;
import com.twoori.contest_server.domain.contest.mapper.ContestDtoForControllerMapperImpl;
import com.twoori.contest_server.domain.contest.mapper.RepositoryMapper;
import com.twoori.contest_server.domain.contest.mapper.RepositoryMapperImpl;
import com.twoori.contest_server.domain.contest.repository.ContestCondition;
import com.twoori.contest_server.domain.contest.repository.ContestRepository;
import com.twoori.contest_server.domain.student.dao.Student;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.global.exception.PermissionDenialException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    @Mock
    private StudentInContestRepository studentInContestRepository;
    @Spy
    private RepositoryMapper repositoryMapper = new RepositoryMapperImpl();

    @DisplayName("대회 시작 10분전에 입장|Success|대회 입장 가능 시간 내에 입장 시도")
    @Test
    void givenContest_whenEnterStudentInContest_thenSaveStudentInCotnestEntityOnce() {
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

    @DisplayName("이미 진입한 상태에서 모종의 문재로 인해 다시 입장 시도|Success|이미 입장한 상태에서 다시 입장 시도")
    @Test
    void givenReEnterStatus_whenEnterStudentInContest_thenSuccessReEntered() {
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

    @DisplayName("대회 시작 1분 후 입장|Success| 최대 1분간 대회 입장 유예 시간 제공")
    @Test
    void givenEnterContest_whenEnterStudentInContest_thenSuccessEnterContest() {
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

    @DisplayName("대회 종료 후 입장 시도|Fail|대회 종료 후 입장 불가")
    @Test
    void givenEndContest_whenEnterStudentInContest_thenThrowExpiredContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = endDateTime.plusMinutes(1);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));
        // when & then
        UUID studentId = student.getId();
        assertThatThrownBy(() -> contestService.enterStudentInContest(studentId, contestId, enterDateTime))
                .isInstanceOf(EndContestException.class);
    }

    @DisplayName("대회 시작 11분전에 입장|Fail|대회 입장 시간 보다 먼저 입장")
    @Test
    void givenNotStartContest_whenEnterStudentInContest_thenThrowEarlyContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.minusMinutes(ENTER_TIME + 1);
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));

        // when & then
        UUID studentId = student.getId();
        assertThatThrownBy(() -> contestService.enterStudentInContest(studentId, contestId, enterDateTime))
                .isInstanceOf(EarlyEnterTimeException.class);
    }

    @DisplayName("대회 자진 포기 후 재입장|Fail|자진 포기한 대회에 재입장 시도")
    @Test
    void givenResignedContest_whenEnterStudentInContest_thenThrowResignedContestException() {
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
        UUID studentId = student.getId();
        assertThatThrownBy(() -> contestService.enterStudentInContest(studentId, contestId, enterDateTime))
                .isInstanceOf(ResignedContestException.class);
    }

    @DisplayName("존재하지 않는 대회에 입장 시도|Fail|대회가 존재하지 않음")
    @Test
    void givenHasNotContest_whenEnterStudentInContest_thenThrowResignedContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime enterDateTime = LocalDateTime.now();
        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.empty());
        // when & then
        UUID studentId = student.getId();
        assertThatThrownBy(() -> contestService.enterStudentInContest(studentId, contestId, enterDateTime))
                .isInstanceOf(NotFoundRegisteredContestException.class);
    }

    @DisplayName("대회 최초진입을 2분뒤에 실행|Fail|대회 입장 시간 초과 후 입장 시도")
    @Test
    void givenReEnterContest_whenReEnterStudentInContest_thenThrowExpiredTimeException() {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(CONTEST_TIME);
        LocalDateTime enterDateTime = startDateTime.plusMinutes(2);

        given(contestRepository.getRegisteredStudentAboutStudent(contestId, student.getId())).willReturn(Optional.of(
                new EnterContestDto(contestId, "name", "hostName", startDateTime, endDateTime)
        ));
        given(contestRepository.isResigned(contestId, student.getId())).willReturn(false);
        given(contestRepository.isEnteredStudentInContest(student.getId(), contestId)).willReturn(false);

        // when & then
        UUID studentId = student.getId();
        assertThatThrownBy(() -> contestService.enterStudentInContest(studentId, contestId, enterDateTime))
                .isInstanceOf(ExpiredTimeException.class);
    }

    @DisplayName("대회 검색|Success|현시점부터 3개월간의 대회 데이터를 모두 조회")
    @Test
    void givenNonParam_whenSearchContest_thenGetContestsIn3Month() {
        // given
        String parameter = "";
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(3);
        ContestCondition condition = new ContestCondition();
        condition.setFrom(from.atStartOfDay());
        condition.setTo(to.atTime(23, 59, 59));
        condition.setParameter(parameter);
        given(contestRepository.searchNotStartedContests(condition)).willReturn(
                IntStream.range(0, 100).mapToObj(i -> new SearchContestDto(
                        contestIds.get(i), "test" + i, "hostName" + i,
                        from.atStartOfDay().plusDays(1), from.atTime(23, 59, 59).plusDays(2))
                ).toList()
        );

        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        assertThat(contests)
                .doesNotContainNull().isNotEmpty().hasSize(100)
                .isSortedAccordingTo(Comparator.comparing(SearchContestDtoForController::startedAt)
                        .thenComparing(SearchContestDtoForController::endedAt))
                .extracting("contestId").containsExactlyElementsOf(contestIds);

    }


    @DisplayName("대회 검색|Success|현시점부터 대회 이름으로 1개월간 데이터 검색")
    @Test
    void givenDueToOneMonth_whenSearchContests_then70OfSortedContestIn100Contests() {
        // given
        String parameter = "search_param";
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(1);
        ContestCondition condition = new ContestCondition();
        condition.setFrom(from.atStartOfDay());
        condition.setTo(to.atTime(23, 59, 59));
        condition.setParameter(parameter);
        given(contestRepository.searchNotStartedContests(condition)).willReturn(
                IntStream.range(0, 100).mapToObj(i -> new SearchContestDto(
                        contestIds.get(i), "contest" + i, "hostName" + i,
                        from.atStartOfDay().plusDays(1), from.atStartOfDay().plusDays(2))).toList()
        );
        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        assertThat(contests)
                .isNotNull().doesNotContainNull().isNotEmpty().hasSize(100)
                .extracting("contestId").containsExactlyElementsOf(contestIds);
    }

    @DisplayName("대회 검색|Success|현시점부터 1개월 이상, 2개월 미만인 모든 대회 검색")
    @Test
    void givenDueTo1To2Month_whenSearchContests_then60OfSortedContests() {
        //given
        String parameter = "";
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now().plusMonths(1);
        LocalDate to = from.plusMonths(2);
        ContestCondition condition = new ContestCondition();
        condition.setFrom(from.atStartOfDay());
        condition.setTo(to.atTime(23, 59, 59));
        condition.setParameter(parameter);
        given(contestRepository.searchNotStartedContests(condition)).willReturn(
                IntStream.range(0, 100).mapToObj(i -> {
                    LocalDateTime startedAt = LocalDateTime.now().plusDays(1);
                    return new SearchContestDto(contestIds.get(i), "contest" + i, "hostName" + i,
                            startedAt, startedAt.plusMinutes(CONTEST_TIME));
                }).toList());

        //when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        //then
        assertThat(contests)
                .isNotNull().doesNotContainNull().isNotEmpty().hasSize(100)
                .extracting("contestId").containsExactlyElementsOf(contestIds);

    }

    @DisplayName("대회 검색|Success|from이 to보다 큰 검색")
    @Test
    void givenFromAfterTo_whenSearchContests_thenListSizeOfZero() {
        // given
        String parameter = "";
        LocalDate from = LocalDate.now().plusMonths(1);
        LocalDate to = from.minusMonths(1);

        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        verify(contestRepository, never()).searchEndOfContests(isA(ContestCondition.class));
        assertThat(contests).isNotNull().isEmpty();
    }

    @DisplayName("대회 검색|Success|from이 현재보다 작은 검색")
    @Test
    void givenFromBeforeNow_whenSearchContests_thenListSizeOfZero() {
        // given
        String parameter = "";
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = from.plusMonths(1);

        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);

        // then
        verify(contestRepository, never()).searchEndOfContests(isA(ContestCondition.class));
        assertThat(contests).isNotNull().isEmpty();
    }

    @DisplayName("기간 범위를 하루로 결정하고 대회를 검색|Success|그날에 진행되는 대회 검색")
    @Test
    void givenFromDayOfOne_whenSearchContests_thenList() {
        // given
        String parameter = "";
        LocalDate now = LocalDate.now();
        ContestCondition condition = new ContestCondition();
        condition.setParameter(parameter);
        condition.setFrom(now.atStartOfDay());
        condition.setTo(now.atTime(23, 59, 59));
        given(contestRepository.searchNotStartedContests(condition)).willReturn(List.of(
                new SearchContestDto(
                        UUID.randomUUID(), "contest", "host",
                        now.atStartOfDay(), now.atStartOfDay().plusMinutes(15))));

        // when
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, now, now);

        // then
        verify(contestRepository, times(1)).searchNotStartedContests(condition);
        assertThat(contests).isNotNull().isNotEmpty();
    }

    @DisplayName("신청한 대회 중 시작하지 않은 대회 조회|Success|대회 검색 결과가 존재")
    @Test
    void givenStudentId_whenGetRegisteredContests_thenReturnContestAfterCurrentTime() {
        // given
        UUID studentId = UUID.randomUUID();
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        given(contestRepository.searchRegisteredContest(isA(ContestCondition.class)))
                .willReturn(IntStream.range(0, 100).mapToObj(i -> new SearchContestDto(contestIds.get(i),
                        "contest" + i, "hostName" + i, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2))).toList());

        // when
        List<RegisteredContestDto> actual = contestService.searchContestForEnterContest(studentId);

        // then
        assertThat(actual)
                .isNotNull().isNotEmpty().hasSize(100)
                .isSortedAccordingTo(Comparator.comparing(RegisteredContestDto::startedAt)
                        .thenComparing(RegisteredContestDto::endedAt))
                .extracting("contestId").containsExactlyElementsOf(contestIds);
    }

    @DisplayName("신청한 대회 중 시작하지 않은 대회 조회|Success|대회 검색 결과 미존재")
    @Test
    void givenStudentId_whenGetRegisteredContests_thenEmptyList() {
        // given
        UUID studentId = UUID.randomUUID();
        given(contestRepository.searchRegisteredContest(isA(ContestCondition.class))).willReturn(List.of());

        // when
        List<RegisteredContestDto> actual = contestService.searchContestForEnterContest(studentId);

        // then
        assertThat(actual).isNotNull().isEmpty();
    }

    @DisplayName("취소 가능한 시간대에 대회 신청 취소 요청|Success| 대회 하루전까지 신청 취소 가능")
    @MethodSource("com.twoori.contest_server.domain.contest.testsources.Parameters#argumentsForCancelTimeAndStartTime")
    @ParameterizedTest
    void givenContest_whenCancelContest_thenExecuteCancelContest(LocalDateTime cancelTime, LocalDateTime startTime) {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        given(contestRepository.getTimesAboutContest(contestId)).willReturn(Optional.of(
                new CancelContestDto(contestId, startTime, startTime.plusMinutes(CONTEST_TIME))));
        doNothing().when(contestRepository).cancelContest(contestId, studentId);

        // when
        contestService.cancelContest(contestId, studentId, cancelTime);

        // then
        verify(contestRepository).cancelContest(contestId, studentId);
    }

    @DisplayName("취소 불가능한 시간대에 신청 취소|Fail| 대회 시작 하루 전까지만 취소 가능")
    @MethodSource("com.twoori.contest_server.domain.contest.testsources.Parameters#argumentsForNotCancelTimeAndStartTime")
    @ParameterizedTest
    void givenContest_whenCancelContest_thenThrowPermissionDenialException(LocalDateTime cancelTime, LocalDateTime startTime) {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        given(contestRepository.getTimesAboutContest(contestId)).willReturn(Optional.of(
                new CancelContestDto(contestId, startTime, startTime.plusMinutes(CONTEST_TIME))
        ));

        // when & then
        assertThatThrownBy(() -> contestService.cancelContest(contestId, studentId, cancelTime))
                .isInstanceOf(PermissionDenialException.class);
        verify(contestRepository, never()).cancelContest(contestId, studentId);
    }

    @DisplayName("대회 중 자진 포기 요청|Success|대회 자진 포기 기록")
    @Test
    void givenContest_whenResignContest_thenExecuteResignContest() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        given(contestRepository.isEnteredStudentInContest(studentId, contestId)).willReturn(true);
        doNothing().when(contestRepository).resignContest(contestId, studentId);

        // when
        contestService.resignContest(contestId, studentId);

        // then
        verify(contestRepository).resignContest(contestId, studentId);
    }

    @DisplayName("대회 중 자진 포기 요청|Fail|대회에 신청하지 않은 상태에서 자진 포기 요청")
    @Test
    void givenContest_whenResignContest_thenThrowNotRegisteredContestException() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        given(contestRepository.isEnteredStudentInContest(studentId, contestId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> contestService.resignContest(contestId, studentId))
                .isInstanceOf(NotRegisteredContestException.class);
        verify(contestRepository, never()).resignContest(contestId, studentId);
    }

    @DisplayName("종료된 대회 검색|Success|종료된 대회들만 검색")
    @Test
    void givenCurrentTimeAndStudentId_whenSearchExpiredContest_thenReturnExpiredContests() {
        // given
        UUID studentId = UUID.randomUUID();
        List<UUID> contestIds = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID()).toList();
        given(contestRepository.searchEndOfContests(isA(ContestCondition.class)))
                .willReturn(IntStream.range(0, 100).mapToObj(i -> new SearchContestDto(contestIds.get(i),
                        "contest" + i, "host" + i,
                        LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2))).toList());

        // when
        List<SearchContestDto> actual = contestService.searchEndOfContests(studentId);

        // then
        assertThat(actual)
                .isNotNull().isNotEmpty().hasSize(100)
                .isSortedAccordingTo(Comparator.comparing(SearchContestDto::startedAt)
                        .thenComparing(SearchContestDto::endedAt))
                .extracting("contestId").containsExactlyElementsOf(contestIds);
    }

    @DisplayName("대회 종료 이전에 명시적으로 종료|Success|입력한 대회 시간으로 종료 기록 후 계산")
    @Test
    void givenEndDateTimeBeforeEndContest_whenEndingContest_thenScoringEndContestTime() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        LocalDateTime endContestAboutStudentDateTime = LocalDateTime.now();
        LocalDateTime startContestDateTime = endContestAboutStudentDateTime.minusMinutes(15);
        LocalDateTime endContestDateTime = endContestAboutStudentDateTime.plusMinutes(1);
        long expectDiffTime = Duration.between(startContestDateTime, endContestAboutStudentDateTime).toSeconds();
        StudentInContest studentInContest = StudentInContest.builder()
                .isEntered(true).isResigned(true)
                .id(new StudentInContestID(studentId, contestId))
                .contest(new Contest(contestId, "code", "name", "hostName",
                        startContestDateTime, endContestDateTime, 0.5, 0.5)).build();
        given(studentInContestRepository.findById(new StudentInContestID(studentId, contestId)))
                .willReturn(Optional.of(studentInContest));
        // when
        long actual = contestService.endingContest(contestId, studentId, endContestAboutStudentDateTime);

        // then
        assertThat(studentInContest).extracting("endContestAt")
                .isEqualTo(endContestAboutStudentDateTime);
        verify(studentInContestRepository, times(1)).save(isA(StudentInContest.class));
        assertThat(actual).isEqualTo(expectDiffTime);
    }

    @DisplayName("대회 시간이 초과되어 클라이언트에서 강제로 대회 종료를 요청|Success|대회 종료 시간으로 기록 후 계산")
    @Test
    void givenEndDateTimeAfterEndContest_whenEndContest_thenScoringEndContestTime() {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        LocalDateTime endContestAboutStudentDateTime = LocalDateTime.of(2023, 10, 10, 15, 30, 0);
        LocalDateTime startContestDateTime = endContestAboutStudentDateTime.minusMinutes(15);
        LocalDateTime endContestDateTime = endContestAboutStudentDateTime.minusSeconds(1);
        long expectDiffTime = Duration.between(startContestDateTime, endContestDateTime).toSeconds();
        StudentInContest studentInContest = StudentInContest.builder()
                .isEntered(true).isResigned(true)
                .id(new StudentInContestID(studentId, contestId))
                .contest(new Contest(contestId, "code", "name", "hostName",
                        startContestDateTime, endContestDateTime, 0.5, 0.5)).build();
        given(studentInContestRepository.findById(new StudentInContestID(studentId, contestId)))
                .willReturn(Optional.of(studentInContest));
        // when
        long actual = contestService.endingContest(contestId, studentId, endContestAboutStudentDateTime);

        // then
        assertThat(studentInContest).extracting("endContestAt")
                .isEqualTo(endContestDateTime);
        verify(studentInContestRepository, times(1)).save(isA(StudentInContest.class));
        assertThat(actual).isEqualTo(expectDiffTime);
    }

    @DisplayName("대회 종료 요청을 n번 보냄|Fail|대회 종료 시간은 첫번째 보낸 것만 기록하고 이외의 요청은 기록하지 않음")
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10})
    @ParameterizedTest(name = "{0} 번 대회 종료 요청을 수행")
    void givenEndContestTime_whenEndContestMore_thenTwiceThenOneIsRecorded(int loopCount) {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        LocalDateTime endContestAboutStudentDateTime = LocalDateTime.now();
        LocalDateTime startContestDateTime = endContestAboutStudentDateTime.minusMinutes(15);
        LocalDateTime endContestDateTime = endContestAboutStudentDateTime.plusMinutes(1);
        long expectDiffTime = Duration.between(startContestDateTime, endContestAboutStudentDateTime).toSeconds();
        StudentInContest studentInContest = StudentInContest.builder()
                .isEntered(true).isResigned(true)
                .id(new StudentInContestID(studentId, contestId))
                .contest(new Contest(contestId, "code", "name", "hostName",
                        startContestDateTime, endContestDateTime, 0.5, 0.5)).build();
        given(studentInContestRepository.findById(new StudentInContestID(studentId, contestId)))
                .willReturn(Optional.of(studentInContest));
        // when & then
        for (int i = 0; i < loopCount; i++) {
            long actual = contestService.endingContest(contestId, studentId, endContestAboutStudentDateTime);
            assertThat(actual).isEqualTo(expectDiffTime);
        }
        verify(studentInContestRepository, times(1)).save(isA(StudentInContest.class));
    }

    @DisplayName("시작한 대회중에 진입 가능한 대회 조회|Success|대회 종료 후 재진입을 위한 데이터 제공")
    @Test
    void givenRunningWhenFindContestIdAboutEnterableContestThenReturnContestId() {
        // given
        UUID studentId = UUID.randomUUID();
        UUID contestId = UUID.randomUUID();
        LocalDateTime runningEndDateTime = LocalDateTime.now().plusMinutes(1);
        given(studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(
                studentId, runningEndDateTime)).willReturn(Optional.of(StudentInContest.builder()
                .id(new StudentInContestID(studentId, contestId))
                .contest(new Contest(contestId, "code", "name", "hostName",
                        LocalDateTime.now().minusMinutes(15), runningEndDateTime,
                        0.5, 0.5))
                .isEntered(true).isResigned(false).build()));

        // when
        UUID actual = contestService.findContestIdAboutEnterableContest(studentId, runningEndDateTime);

        // then
        assertThat(actual).isEqualTo(contestId);
    }

    @DisplayName("시작한 대회중에 진입 가능한 대회 조회|Fail|진입 가능한 대회가 없음")
    @Test
    void givenRunningWhenFindContestIdAboutEnterableContestThenThroeNotFoundRegisteredContestException() {
        // given
        UUID studentId = UUID.randomUUID();
        LocalDateTime runningEndDateTime = LocalDateTime.now().plusMinutes(1);
        given(studentInContestRepository.findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(
                studentId, runningEndDateTime)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contestService.findContestIdAboutEnterableContest(studentId, runningEndDateTime))
                .isInstanceOf(NotFoundRegisteredContestException.class);
    }
}