package com.twoori.contest_server.domain.student.dto;

import com.twoori.contest_server.domain.student.dao.Student;
import lombok.Builder;

import java.util.UUID;


@Builder
public record StudentDto(UUID studentId,
                         String nickname,
                         String accessToken,
                         String refreshToken) {
    public static StudentDto daoToDto(Student dao) {
        return new StudentDto(
                dao.getStudentId(),
                dao.getNickname(),
                dao.getAccessToken(),
                dao.getRefreshToken()
        );
    }
}