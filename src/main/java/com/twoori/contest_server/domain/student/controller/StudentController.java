package com.twoori.contest_server.domain.student.controller;

import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.service.StudentService;
import com.twoori.contest_server.domain.student.vo.ContestStatus;
import com.twoori.contest_server.domain.student.vo.ProblemStatus;
import com.twoori.contest_server.domain.student.vo.StudentStatusVO;
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
    private final StudentService studentService;
    private ContestService contestService;

    public StudentController(StudentJwtProvider studentJwtProvider, StudentService studentService) {
        this.studentJwtProvider = studentJwtProvider;
        this.studentService = studentService;
    }

    @GetMapping("/v1/student/{student_id}/mock_token")
    public AuthToken getMockTokens(@PathVariable("student_id") String rawStudentID) {
        UUID studentID = UUID.fromString(rawStudentID);
        return studentJwtProvider.createAuthToken(StudentDto.builder()
                .id(studentID)
                .build());
    }

    @GetMapping("/v1/student/status")
    public StudentStatusVO getStudentStatus(@RequestHeader("Authorization") String rawToken) {
        StudentDto studentDto = studentJwtProvider.validateAccessToken(rawToken);
        LocalDateTime now = LocalDateTime.now();
        UUID contestId = contestService.findContestIdAboutEnterableContest(studentDto.id(), now);
        System.out.println("test");
        return new StudentStatusVO(new ContestStatus(
                contestId, new ProblemStatus(0L, 0L, 0L)));
    }
}
