package com.twoori.contest_server.domain.student.controller;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.problem.dto.ProblemIdDto;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import com.twoori.contest_server.domain.student.service.TrackingStudentService;
import com.twoori.contest_server.domain.student.vo.ContestStatus;
import com.twoori.contest_server.domain.student.vo.ProblemStatus;
import com.twoori.contest_server.domain.student.vo.StudentStatusVO;
import com.twoori.contest_server.global.security.SecurityUtil;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.vo.AuthToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class StudentController {

    private final StudentJwtProvider studentJwtProvider;
    private final ContestService contestService;
    private final TrackingStudentService trackingStudentService;
    private final ProblemService problemService;
    private final SecurityUtil securityUtil;

    public StudentController(StudentJwtProvider studentJwtProvider,
                             ContestService contestService,
                             TrackingStudentService trackingStudentService,
                             ProblemService problemService, SecurityUtil securityUtil) {
        this.studentJwtProvider = studentJwtProvider;
        this.contestService = contestService;
        this.trackingStudentService = trackingStudentService;
        this.problemService = problemService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/v1/student/{student_id}/mock_token")
    public AuthToken getMockTokens(@PathVariable("student_id") String rawStudentID) {
        UUID studentID = UUID.fromString(rawStudentID);
        return studentJwtProvider.createAuthToken(StudentDto.builder()
                .studentId(studentID)
                .build());
    }

    @GetMapping("/v1/contest/student/status")
    public StudentStatusVO getStudentStatus(@RequestHeader("Authorization") String rawToken) {
        StudentDto studentDto = securityUtil.validateAuthorization(rawToken);
        EnterContestDto contestDto = contestService.findContestIdAboutEnterableContest(studentDto.studentId(), LocalDateTime.now());
        StudentInContestIdDto studentInContestID = new StudentInContestIdDto(contestDto.contestId(), studentDto.studentId());
        ProblemIdDto status = trackingStudentService.getStudentStatusInContest(studentInContestID);
        int countOfTry = problemService.getCountOfTry(studentInContestID, status);
        return new StudentStatusVO(new ContestStatus(
                contestDto.contestId(), contestDto.startDateTime(), contestDto.endDateTime()
                , new ProblemStatus(status.problemId(), status.contentId(), countOfTry)));
    }
}
