package com.twoori.contest_server.domain.contest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDtoForController;
import com.twoori.contest_server.domain.contest.excpetion.ForbiddenRegisterContestException;
import com.twoori.contest_server.domain.contest.excpetion.NotCancelRegisterContest;
import com.twoori.contest_server.domain.contest.excpetion.NotFoundRegisteredContestException;
import com.twoori.contest_server.domain.contest.excpetion.NotRegisteredContestException;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.RegisteredContestVO;
import com.twoori.contest_server.domain.contest.vo.SearchContestVO;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import com.twoori.contest_server.domain.student.service.TrackingStudentService;
import com.twoori.contest_server.global.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ContestControllerTest {
    private final UUID studentId = UUID.randomUUID();
    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    @MockBean
    private ContestService contestService;
    @MockBean
    private ProblemService problemService;

    @MockBean
    private TrackingStudentService trackingStudentService;
    @Autowired
    private MockMvc mvc;



    @DisplayName("대회 검색|Success|검색 결과 totalContestCount 중 registeredContestCount 건이 신청한 대회")
    @MethodSource("com.twoori.contest_server.domain.contest.testsources.Parameters#argumentsForSearchContest")
    @ParameterizedTest
    void givenSearchParameter_whenSearchContests_thenTotalContestCountOfContestInRegisteredContestCountOfContest(
            int registeredContestCount, int totalContestCount
    ) throws Exception {
        // given
        String parameter = "";
        List<UUID> contestIds = IntStream.range(0, totalContestCount).mapToObj(i -> UUID.randomUUID()).toList();
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusMonths(3);
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
        ResultActions actual = mvc.perform(get("/contest")
                .param("from", from.toString())
                .param("to", to.toString())
                .param("search", parameter)
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        byte[] rawJson = actual.andReturn().getResponse().getContentAsByteArray();
        Map<String, Object> body = objectMapper.readValue(rawJson, new TypeReference<>() {
        });
        List<SearchContestVO> contests = objectMapper.convertValue(body.get("contests"), new TypeReference<>() {
        });
        assertThat(contests)
                .isNotNull().hasSize(totalContestCount)
                .filteredOn(SearchContestVO::isRegistered).hasSize(registeredContestCount);
    }

    @DisplayName("신청한 대회중 시작하지 않은 대회 조회|Success|검색 결과 20건이 반환")
    @Test
    void givenNonParameter_whenGetRegisteredContest_thenList20() throws Exception {
        // given
        List<UUID> contestIds = IntStream.range(0, 20).mapToObj(i -> UUID.randomUUID()).toList();


        given(contestService.searchContestForEnterContest(studentId)).willReturn(
                IntStream.range(0, 20).mapToObj(i -> new RegisteredContestDto(contestIds.get(i),
                        "contest name" + i,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(10))).toList());

        // when
        ResultActions actual = mvc.perform(get("/contest/registered")
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        byte[] rawJson = actual.andReturn().getResponse().getContentAsByteArray();
        Map<String, Object> body = objectMapper.readValue(rawJson, new TypeReference<>() {
        });
        List<RegisteredContestVO> contests = objectMapper.convertValue(body.get("contests"), new TypeReference<>() {
        });
        assertThat(contests).isNotNull().hasSize(20)
                .allMatch(Objects::nonNull);
    }

    @DisplayName("취소 가능한 시간대에 대회 신청 취소 요청|Success| 대회 하루전까지 신청 취소 가능")
    @Test
    void givenRegisteredContest_whenCancelContest_then200Status() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        doNothing().when(contestService).cancelContest(eq(contestId), eq(studentId), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/cancel", contestId)
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":200,\"message\":\"ok\"}"));
    }

    @DisplayName("취소 불가능한 시간대에 신청 취소|Fail| 대회 시작 하루 전까지만 취소 가능")
    @Test
    void givenRegisteredContest_whenCancelContest_then404Status() throws Exception {
        UUID contestId = UUID.randomUUID();
        doThrow(new NotCancelRegisterContest(studentId, contestId))
                .when(contestService).cancelContest(eq(contestId), eq(studentId), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/cancel", contestId)
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":403,\"message\":\"not cancel time\"}"));
    }

    @DisplayName("잘못된 파라미터값으로 대회 신청 취소 요청|Fail| 파라미터 변환 실패")
    @MethodSource("com.twoori.contest_server.domain.contest.testsources.Parameters#argumentsForWrongContestIds")
    @ParameterizedTest
    void givenWrongParameter_whenCancelContest_thenFail(Object contestId) throws Exception {
        // given


        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/cancel", contestId)
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":400,\"message\":\"invalid parameter\"}"));
    }

    @DisplayName("존재하지 않는 contestId 혹은 studentId로 요청| Fail| 존재하지 않는 파라미터")
    @Test
    void givenNotFoundIds_whenCancelContest_thenFail() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        doThrow(new NotFoundRegisteredContestException(studentId, contestId))
                .when(contestService).cancelContest(eq(contestId), eq(studentId), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/cancel", contestId)
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":404,\"message\":\"not found contest\"}"));
    }

    @DisplayName("대회 자진 포기 요청|Success|포기 완료")
    @Test
    void givenRequestResign_whenResignContest_then200Status() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        doNothing().when(contestService).resignContest(contestId, studentId);
        doNothing().when(trackingStudentService).quitContest(new StudentInContestIdDto(contestId, studentId));

        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/resign", contestId)
                .param("student_id", String.valueOf(studentId))
                .param("contest_id", contestId.toString()));

        // then
        verify(trackingStudentService, times(1))
                .quitContest(new StudentInContestIdDto(contestId, studentId));
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":200,\"message\":\"ok\"}"));
    }

    @DisplayName("잘못된 파라미터로 대회 자진 포기 요청|Fail|잘못된 파라미터")
    @MethodSource("com.twoori.contest_server.domain.contest.testsources.Parameters#argumentsForWrongContestIds")
    @ParameterizedTest
    void givenWrongParameter_whenResignContest_then400Status(Object contestId) throws Exception {
        // given


        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/resign", contestId)
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":400,\"message\":\"invalid parameter\"}"));
    }

    @DisplayName("신청하지도 않는 대회에 자진 포기|Fail|불가능한 상황에서 대회 신청 취소 요청")
    @Test
    void givenNotRegisteredContest_whenResignContest_then400Status() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        doThrow(new NotRegisteredContestException(studentId, contestId))
                .when(contestService).resignContest(contestId, studentId);

        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/resign", contestId)
                .param("student_id", String.valueOf(studentId)));

        // then
        actual.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":400,\"message\":\"not registered contest\"}"));
    }

    @DisplayName("종료된 대회 검색|Success|검색 결과 totalContestCount 중 registeredContestCount 건이 신청한 대회")
    @Test
    void givenValidateToken_whenSearchEndContests_then100OfContests() throws Exception {
        // given
        given(contestService.searchEndOfContests(studentId)).willReturn(IntStream.range(0, 10)
                .mapToObj(i -> new SearchContestDto(UUID.randomUUID(),
                        "contest name" + i, "host" + i,
                        LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusMinutes(15))).toList());
        // when
        ResultActions actual = mvc.perform(get("/contests/end")
                .param("student_id", String.valueOf(studentId)));

        // then
        byte[] rawJson = actual.andReturn().getResponse().getContentAsByteArray();
        Map<String, Object> body = objectMapper.readValue(rawJson, new TypeReference<>() {
        });
        List<SearchContestVO> contests = objectMapper.convertValue(body.get("contests"), new TypeReference<>() {
        });
        actual.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertThat(contests).isNotNull().hasSize(10);
    }


    @DisplayName("PUT /contest/{contest_id}/end|명시적 대회 종료 요청| 걸린 시간 제공")
    @Test
    void givenRequestEndContest_whenEndContest_thenReturnScoreAndTime() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMinutes(10);
        long diffTime = Duration.between(startDateTime, endDateTime).getSeconds();
        double average = 0.7;
        given(contestService.endingContest(eq(contestId), eq(studentId), isA(LocalDateTime.class))).willReturn(diffTime);
        given(problemService.createAverageScore(contestId, studentId)).willReturn(average);
        doNothing().when(trackingStudentService).quitContest(new StudentInContestIdDto(contestId, studentId));

        // when
        ResultActions actual = mvc.perform(put("/contest/{contest_id}/end", contestId)
                .param("student_id", String.valueOf(studentId)));


        // then
        verify(trackingStudentService, times(1))
                .quitContest(new StudentInContestIdDto(contestId, studentId));
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":200,\"message\":\"ok\"," +
                        "\"average\":" + average + ",\"diff_time\": " + diffTime + "}"));
    }

    @DisplayName("POST /contest/{contest_id}/register|Success|대회 신청 성공")
    @Test
    void givenRequestRegisterContest_whenRegisterContest_then200Status() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        String authCode = "authCode";
        willDoNothing().given(contestService).registerContestByUser(eq(contestId), eq(studentId), eq(authCode), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(post("/contest/{contest_id}/register", contestId)
                .param("student_id", String.valueOf(studentId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"auth_code\":\"" + authCode + "\"}"));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":200,\"message\":\"ok\"}"));
    }


    @DisplayName("POST /contest/{contest_id}/register|Fail|올바르지 않은 인증 코드")
    @Test
    void givenInvalidateAuthCode_whenRegisterContest_then400Status() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        String authCode = "authCode";
        doThrow(new BadRequestException("not match auth code"))
                .when(contestService).registerContestByUser(eq(contestId), eq(studentId), eq(authCode), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(post("/contest/{contest_id}/register", contestId)
                .param("student_id", String.valueOf(studentId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"auth_code\":\"" + authCode + "\"}"));

        // then
        actual.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":400,\"message\":\"not match auth code\"}"));
    }

    @DisplayName("POST /contest/{contest_id}/register|Fail|시간이 지난 대회 신청")
    @Test
    void givenExpiredContest_whenRegisterContest_then403Status() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        String authCode = "authCode";
        doThrow(new ForbiddenRegisterContestException("expired register date"))
                .when(contestService).registerContestByUser(eq(contestId), eq(studentId), eq(authCode), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(post("/contest/{contest_id}/register", contestId)
                .param("student_id", String.valueOf(studentId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"auth_code\":\"" + authCode + "\"}"));

        // then
        actual.andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":403,\"message\":\"expired register date\"}"));
    }

    @DisplayName("POST /contest/{contest_id}/register|Fail|취소한 대회 재신청")
    @Test
    void givenCancelContest_whenRegisterContest_then403Status() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        String authCode = "authCode";
        doThrow(new ForbiddenRegisterContestException("registered contest"))
                .when(contestService).registerContestByUser(eq(contestId), eq(studentId), eq(authCode), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(post("/contest/{contest_id}/register", contestId)
                .param("student_id", String.valueOf(studentId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"auth_code\":\"" + authCode + "\"}"));

        // then
        actual.andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":403,\"message\":\"registered contest\"}"));
    }
}