package com.twoori.contest_server.domain.student.controller;

import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.vo.AuthToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class StudentController {

    private final StudentJwtProvider studentJwtProvider;

    public StudentController(StudentJwtProvider studentJwtProvider) {
        this.studentJwtProvider = studentJwtProvider;
    }

    @GetMapping("/v1/student/{student_id}/mock_token")
    public AuthToken getMockTokens(@PathVariable("student_id") String rawStudentID) {
        UUID studentID = UUID.fromString(rawStudentID);
        return studentJwtProvider.createAuthToken(StudentDto.builder()
                .id(studentID)
                .build());
    }
}
