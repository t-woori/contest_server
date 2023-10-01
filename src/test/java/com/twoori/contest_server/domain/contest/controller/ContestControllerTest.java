package com.twoori.contest_server.domain.contest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDtoForController;
import com.twoori.contest_server.domain.contest.excpetion.NotCancelRegisterContest;
import com.twoori.contest_server.domain.contest.excpetion.NotFoundContestException;
import com.twoori.contest_server.domain.contest.excpetion.NotRegisteredContestException;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.SearchContestVO;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ContestControllerTest {
    private final String mockToken = "Bearer MockToken";
    private final UUID studentId = UUID.randomUUID();
    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    @MockBean
    private ContestService contestService;
    @MockBean
    private SecurityUtil securityUtil;
    @Autowired
    private MockMvc mvc;


    private static Stream<Arguments> argumentsForSearchContest() {
        return Stream.of(
                Arguments.of(0, 100),
                Arguments.of(1, 100),
                Arguments.of(10, 100),
                Arguments.of(100, 100)
        );
    }

    @BeforeEach
    void beforeAll() {
        given(securityUtil.validateAuthorization(mockToken)).willReturn(new StudentDto(studentId, "mockName", "mockEmail", "mockPhoneNumber", "mockKakaoAccToken", "mockKakaoRefToken"));
    }

    @DisplayName("대회 검색|Success|검색 결과 totalContestCount 중 registeredContestCount 건이 신청한 대회")
    @MethodSource("argumentsForSearchContest")
    @ParameterizedTest
    void givenSearchParameterWhenSearchContestsThenTotalContestCountOfContestInRegisteredContestCountOfContest(
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
        ResultActions actual = mvc.perform(get("/v1/contest")
                .param("from", from.toString())
                .param("to", to.toString())
                .param("search", parameter)
                .header("Authorization", mockToken));

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
    void givenNonParameterWhenGetRegisteredContestThenList20() throws Exception {
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
                IntStream.range(0, 20).mapToObj(i -> new RegisteredContestDto(contestIds.get(i),
                        "contest name" + i,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(10))).toList());

        // when
        ResultActions actual = mvc.perform(get("/v1/contest/registered")
                .header("Authorization", mockHeader));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        byte[] rawJson = actual.andReturn().getResponse().getContentAsByteArray();
        Map<String, Object> body = objectMapper.readValue(rawJson, new TypeReference<>() {
        });
        List<SearchContestVO> contests = objectMapper.convertValue(body.get("contests"), new TypeReference<>() {
        });
        assertThat(contests).isNotNull().hasSize(20);
    }

    @DisplayName("취소 가능한 시간대에 대회 신청 취소 요청|Success| 대회 하루전까지 신청 취소 가능")
    @Test
    void givenRegisteredContestWhenCancelContestThenSuccess() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        doNothing().when(contestService).cancelContest(eq(contestId), eq(studentId), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/cancel", contestId)
                .header("Authorization", mockToken));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":200,\"message\":\"ok\"}"));
    }

    @DisplayName("취소 불가능한 시간대에 신청 취소|Fail| 대회 시작 하루 전까지만 취소 가능")
    @Test
    void givenRegisteredContestWhenCancelContestThenFail() throws Exception {
        UUID contestId = UUID.randomUUID();
        doThrow(new NotCancelRegisterContest(studentId, contestId))
                .when(contestService).cancelContest(eq(contestId), eq(studentId), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/cancel", contestId)
                .header("Authorization", mockToken));

        // then
        actual.andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":403,\"message\":\"not cancel time\"}"));
    }

    @DisplayName("잘못된 파라미터값으로 대회 신청 취소 요청|Fail| 파라미터 변환 실패")
    @MethodSource("com.twoori.contest_server.domain.contest.testsources.Parameters#argumentsForWrongContestIds")
    @ParameterizedTest
    void givenWrongParameterWhenCancelContestThenFail(Object contestId) throws Exception {
        // given
        String mockHeader = "";
        given(securityUtil.validateAuthorization(mockHeader)).willReturn(new StudentDto(UUID.randomUUID(),
                "name", "email", "phoneNumber", "accessToken", "refreshToken"));

        // when
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/cancel", contestId)
                .header("Authorization", mockHeader));

        // then
        actual.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":400,\"message\":\"invalid parameter\"}"));
    }

    @DisplayName("존재하지 않는 contestId 혹은 studentId로 요청| Fail| 존재하지 않는 파라미터")
    @Test
    void givenNotFoundIdsWhenCancelContestThenFail() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        String mockHeader = "";
        given(securityUtil.validateAuthorization(mockHeader)).willReturn(new StudentDto(studentId,
                "name", "email", "phoneNumber", "accessToken", "refreshToken"));
        doThrow(new NotFoundContestException(studentId, contestId))
                .when(contestService).cancelContest(eq(contestId), eq(studentId), isA(LocalDateTime.class));

        // when
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/cancel", contestId)
                .header("Authorization", mockHeader));

        // then
        actual.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":404,\"message\":\"not found contest\"}"));
    }

    @DisplayName("대회 자진 포기 요청|Success|포기 완료")
    @Test
    void givenRequestResignWhenResignContestThenSuccess() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        doNothing().when(contestService).resignContest(contestId, studentId);

        // when
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/resign", contestId)
                .header("Authorization", mockToken)
                .param("contest_id", contestId.toString()));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":200,\"message\":\"ok\"}"));
    }

    @DisplayName("잘못된 파라미터로 대회 자진 포기 요청|Fail|잘못된 파라미터")
    @MethodSource("com.twoori.contest_server.domain.contest.testsources.Parameters#argumentsForWrongContestIds")
    @ParameterizedTest
    void givenWrongParameterWhenResignContestThenFail(Object contestId) throws Exception {
        // given
        String mockHeader = "";
        given(securityUtil.validateAuthorization(mockHeader)).willReturn(new StudentDto(UUID.randomUUID(),
                "name", "email", "phoneNumber", "accessToken", "refreshToken"));

        // when
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/resign", contestId)
                .header("Authorization", mockHeader));

        // then
        actual.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":400,\"message\":\"invalid parameter\"}"));
    }

    @DisplayName("신청하지도 않는 대회에 자진 포기|Fail|불가능한 상황에서 대회 신청 취소 요청")
    @Test
    void givenNotRegisteredContestWhenResignContestThenFail() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        doThrow(new NotRegisteredContestException(studentId, contestId))
                .when(contestService).resignContest(contestId, studentId);

        // when
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/resign", contestId)
                .header("Authorization", mockToken));

        // then
        actual.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"status\":400,\"message\":\"not registered contest\"}"));
    }
}