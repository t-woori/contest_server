package com.twoori.contest_server.domain.student.dto;

import com.twoori.contest_server.domain.student.dao.Student;
import lombok.Builder;

import java.util.UUID;


@Builder
public record StudentDto(UUID id,
                         String name,
                         String email,
                         String phoneNumber,
                         String accessToken,
                         String refreshToken) {
    public static StudentDto daoToDto(Student dao) {
        return new StudentDto(
                dao.getId(),
                dao.getName(),
                dao.getEmail(),
                dao.getPhoneNumber(),
                dao.getAccessToken(),
                dao.getRefreshToken()
        );
    }
}