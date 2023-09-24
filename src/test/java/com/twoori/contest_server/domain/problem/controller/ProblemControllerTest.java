package com.twoori.contest_server.domain.problem.controller;

import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.domain.problem.exceptions.NotFoundProblemException;
import com.twoori.contest_server.domain.problem.repository.ContentDto;
import com.twoori.contest_server.domain.problem.repository.ProblemDto;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProblemControllerTest {


    private final String mockToken = "Bearer MockToken";
    @InjectMocks
    private ProblemController problemController;
    @Mock
    private ProblemService problemService;
    @Mock
    private SecurityUtil securityUtil;
    private MockMvc mvc;

    @BeforeEach
    void beforeAll() {
        this.mvc = MockMvcBuilders.standaloneSetup(problemController).build();
        given(securityUtil.validateAuthorization(mockToken)).willReturn(new StudentDto(UUID.randomUUID(), "mockName", "mockEmail", "mockPhoneNumber", "mockKakaoAccToken", "mockKakaoRefToken"));
    }

    @DisplayName("GET /v1/contest/{contest_id}/problem/{problem_id}|Success|문제 제공 성공")
    @MethodSource("com.twoori.contest_server.domain.problem.repository.Parameters#parametersOfExistsProblemId")
    @ParameterizedTest
    void givenProblemIdWhenGetProblemThenProblemInfo(UUID contestId, Long noOfProblemInContest) throws Exception {
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
                        "/v1/problem"
                ).param("problem_id", noOfProblemInContest.toString()).
                param("contest_id", contestId.toString()).
                header("Authorization", mockToken));
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.problem_id").value(noOfProblemInContest))
                .andExpect(jsonPath("$.problem_type").value(PROBLEM_TYPE.BLANK.name()))
                .andExpect(jsonPath("$.chapter_type").value(CHAPTER_TYPE.CAFFEE.name()))
                .andExpect(jsonPath("$.problem_grade").value(GRADE.ELEMENTARY.name()))
                .andExpect(jsonPath("$.image_url").value("mockImageURL"))
                .andExpect(jsonPath("$.contents[0].content_id").value(0L))
                .andExpect(jsonPath("$.contents[0].answer").value("mockAnswer"))
                .andExpect(jsonPath("$.contents[0].pre_script").value("mockPreScript"))
                .andExpect(jsonPath("$.contents[0].question").value("mockQuestion"))
                .andExpect(jsonPath("$.contents[0].post_script").value("mockPostScript"))
                .andExpect(jsonPath("$.contents[0].hint").value("mockHint"));

    }

    @DisplayName("GET /v1/contest/{contest_id}/problem/{problem_id}|Fail|존재하지 문제 조회")
    @MethodSource("com.twoori.contest_server.domain.problem.repository.Parameters#parametersOfNotExistsProblemId")
    @ParameterizedTest
    void givenNotFoundExceptionWhenGetProblemThenStatus404(UUID contestId, Long noOfProblemInContest) throws Exception {
        // given
        given(problemService.getProblem(contestId, noOfProblemInContest)).willThrow(
                new NotFoundProblemException(contestId, noOfProblemInContest));

        // when & then
        ResultActions actual = mvc.perform(MockMvcRequestBuilders.get(
                        "/v1/problem"
                ).param("problem_id", "0").
                header("Authorization", mockToken));
        actual.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("not found problem"));
    }

}