package com.twoori.contest_server.domain.student.controller;

import com.twoori.contest_server.domain.contest.excpetion.NotFoundRegisteredContestException;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.problem.dto.ProblemIdDto;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import com.twoori.contest_server.domain.student.service.TrackingStudentService;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    private final String mockToken = "Bearer MockToken";
    private final UUID studentId = UUID.randomUUID();
    @MockBean
    private TrackingStudentService trackingStudentService;
    @MockBean
    private ContestService contestService;
    @MockBean
    private ProblemService problemService;
    @MockBean
    private StudentJwtProvider studentJwtProvider;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setupStudent() {
        given(studentJwtProvider.validateAccessToken(mockToken)).willReturn(new StudentDto(studentId,
                "mockName", "mockKakaoAccToken", "mockKakaoRefToken"));
    }

    @DisplayName("진행중인 대회 검색|Success|진입가능한 대회 및 문제 기록 정보 제공")
    @Test
    void givenAccessibleStudent_whenGetStudentStatus_thenReturn200AndAccessibleContestAndProblemStatus() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        UUID contestId = UUID.randomUUID();
        StudentInContestIdDto studentInContestIdDto = new StudentInContestIdDto(studentId, contestId);
        ProblemIdDto problemIdDto = new ProblemIdDto(0, 0);
        int countOfTry = 0;
        given(contestService.findContestIdAboutEnterableContest(eq(studentId), isA(LocalDateTime.class))).willReturn(contestId);
        given(trackingStudentService.getStudentStatusInContest(studentInContestIdDto)).willReturn(problemIdDto);
        given(problemService.getCountOfTry(studentInContestIdDto, problemIdDto)).willReturn(countOfTry);

        // when
        ResultActions actual = mvc.perform(get("/v1/contest/student/status")
                .header("Authorization", mockToken));

        // then
        actual.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{" +
                        "\"running_contest\":{" +
                        "    \"contest_id\": " + contestId + "," +
                        "    \"status\":{" +
                        "      \"problem_id\":" + problemIdDto.problemId() + "," +
                        "      \"content_id\":" + problemIdDto.contentId() + "," +
                        "      \"count_of_try\":" + countOfTry +
                        "}}}"));
    }

    @DisplayName("진행중인 대회 검색|Success|진입가능한 대회가 없을 때")
    @Test
    void givenNotAccessibleStudent_whenGetStudentStatus_thenReturn404() throws Exception {
        // given
        given(contestService.findContestIdAboutEnterableContest(eq(studentId), isA(LocalDateTime.class))).willThrow(new NotFoundRegisteredContestException(studentId, null));

        // when
        ResultActions actual = mvc.perform(get("/v1/contest/student/status")
                .header("Authorization", mockToken));

        // then
        actual.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{" +
                        "\"status\":404," +
                        "\"message\": \"not found contest\"" +
                        "}"));
    }
}
