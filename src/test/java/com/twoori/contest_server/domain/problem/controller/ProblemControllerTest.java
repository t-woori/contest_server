package com.twoori.contest_server.domain.problem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoori.contest_server.domain.problem.dto.ContentDto;
import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.dto.SolvedProblemDto;
import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.domain.problem.exceptions.NotFoundProblemException;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.problem.vo.SolvedProblemVO;
import com.twoori.contest_server.domain.student.service.TrackingStudentService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProblemControllerTest {

    @MockBean
    private ProblemService problemService;

    @MockBean
    private TrackingStudentService trackingStudentService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    private final UUID studentId = UUID.randomUUID();

    @DisplayName("GET /v1/contest/{contest_id}/problem/{problem_id}|Success|문제 제공 성공")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#argumentsOfExistsProblemId")
    @ParameterizedTest
    void givenProblemId_whenGetProblem_thenProblemInfo(UUID contestId, Long noOfProblemInContest) throws Exception {
        // given
        given(problemService.getProblem(contestId, noOfProblemInContest)).willReturn(
                new ProblemDto(
                        noOfProblemInContest,
                        PROBLEM_TYPE.BLANK,
                        CHAPTER_TYPE.CAFFEE,
                        GRADE.ELEMENTARY,
                        "mockImageURL",
                        List.of(new ContentDto(0L,
                                "mockAnswer",
                                "mockPreScript",
                                "mockQuestion",
                                "mockPostScript",
                                "mockHint"))
                ));

        // when & then

        ResultActions actual = mvc.perform(MockMvcRequestBuilders.get(
                "/v1/contest/{contest_id}/problem/{problem_id}"
                , contestId.toString(), noOfProblemInContest.toString()
        ).param("student_id", String.valueOf(studentId)));
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.problem_id").value(noOfProblemInContest))
                .andExpect(jsonPath("$.problem_type").value(PROBLEM_TYPE.BLANK.getValue()))
                .andExpect(jsonPath("$.chapter_type").value(CHAPTER_TYPE.CAFFEE.getValue()))
                .andExpect(jsonPath("$.problem_grade").value(GRADE.ELEMENTARY.getValue()))
                .andExpect(jsonPath("$.image_url").value("mockImageURL"))
                .andExpect(jsonPath("$.contents[0].content_id").value(0L))
                .andExpect(jsonPath("$.contents[0].answer").value("mockAnswer"))
                .andExpect(jsonPath("$.contents[0].pre_script").value("mockPreScript"))
                .andExpect(jsonPath("$.contents[0].question").value("mockQuestion"))
                .andExpect(jsonPath("$.contents[0].post_script").value("mockPostScript"))
                .andExpect(jsonPath("$.contents[0].hint").value("mockHint"))
                .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("GET /v1/contest/{contest_id}/problem/{problem_id}|Fail|존재하지 문제 조회")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#argumentsOfNotExistsProblemId")
    @ParameterizedTest
    void givenNotFoundException_whenGetProblemThe404Status(UUID contestId, Long noOfProblemInContest) throws Exception {
        // given
        given(problemService.getProblem(contestId, noOfProblemInContest)).willThrow(
                new NotFoundProblemException(contestId, noOfProblemInContest));

        // when & then
        ResultActions actual = mvc.perform(MockMvcRequestBuilders.get(
                        "/v1/contest/{contest_id}/problem/{problem_id}"
                        , contestId.toString(), noOfProblemInContest.toString()
        ).param("student_id", String.valueOf(studentId)));
        actual.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("not found problem"));
    }

    @DisplayName("GET /v1/contest/{contest_id}/problem/{problem_id}|Fail|null 혹은 유효하지 않는 값 존제")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#argumentsOfInvalidProblemId")
    @ParameterizedTest
    void givenInvalidateParam_whenThrowInvalidateExceptionOnValidator_then400Status(Object contestId, Object noOfProblemInContest) throws Exception {

        // when & then
        ResultActions actual = mvc.perform(MockMvcRequestBuilders.get(
                        "/v1/contest/{contest_id}/problem/{problem_id}"
                        , contestId, noOfProblemInContest
        ).param("student_id", String.valueOf(studentId)));
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid parameter"));
    }

    @DisplayName("PUT /v1/contest/{contest_id}/student/{student_id}/problem/score|Success|문제 제출 성공")
    @Test
    void givenSolvedProblem_whenUpdateSolvedProblem_thenExecuteOnceUpdateMaxScoreAboutProblem() throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        SolvedProblemVO solvedProblemVO = new SolvedProblemVO(0L, 0L, 0.70);
        // when & then
        ResultActions actual = mvc.perform(put("/v1/contest/{contest_id}/student/{student_id}/problem/score",
                contestId,
                studentId).param("student_id", String.valueOf(studentId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solvedProblemVO))
        );
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
        verify(problemService, times(1)).updateMaxScoreAboutProblem(
                new SolvedProblemDto(contestId, studentId, solvedProblemVO.problemId(), solvedProblemVO.contentId(), solvedProblemVO.score()));
    }

    @DisplayName("GET /v1/contest/{contest_id}/total_status | 10명이 1문제를 풀고있는 상황 조회 | Success | 1문제에 10명이 존재")
    @MethodSource("com.twoori.contest_server.domain.problem.testsources.Parameters#argumentsForTotalStatus")
    @ParameterizedTest
    void givenRequestTotalStatusApi_whenGetStatus_thenReturnOfList(List<Long> countOfProblems) throws Exception {
        // given
        UUID contestId = UUID.randomUUID();
        given(trackingStudentService.getTotalStatus()).willReturn(countOfProblems);

        // when
        ResultActions actual = mvc.perform(get("/v1/contest/{contest_id}/total_status", contestId)
                .param("student_id", String.valueOf(studentId)));
        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}