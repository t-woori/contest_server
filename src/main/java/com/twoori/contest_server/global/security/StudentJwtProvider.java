package com.twoori.contest_server.global.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.service.StudentService;
import com.twoori.contest_server.global.exception.PermissionDenialException;
import com.twoori.contest_server.global.vo.AuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class StudentJwtProvider {
    private final StudentService studentService;
    private final long ACCESS_TOKEN_EXP = 10 * 60 * 1000L;
    private final long REFRESH_TOKEN_EXP = 14 * 24 * 60 * 60 * 1000L;
    private final Algorithm jwtAlgorithm;
    private final JWTVerifier verifier;

    public StudentJwtProvider(StudentService studentService, @Value("{jwt.secret}") String jwtSecretKey) {
        this.studentService = studentService;
        this.jwtAlgorithm = Algorithm.HMAC512(jwtSecretKey);
        this.verifier = JWT.require(jwtAlgorithm).build();
    }

    public AuthToken createAuthToken(StudentDto studentDto) {
        String accessToken = createAccessToken(studentDto);
        String refreshToken = createRefreshToken(studentDto, accessToken);
        return new AuthToken(accessToken, refreshToken);
    }

    private String createAccessToken(StudentDto studentDto) {
        return JWT.create()
                .withClaim("id", studentDto.id().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .withJWTId(UUID.randomUUID().toString())
                .sign(jwtAlgorithm);
    }

    private String createRefreshToken(StudentDto studentDto, String accessToken) {
        return JWT.create()
                .withClaim("id", studentDto.id().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                .withClaim("accessToken", accessToken)
                .sign(jwtAlgorithm);
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            DecodedJWT decodedJWT = verifier.verify(accessToken);
            if (decodedJWT.getExpiresAt().before(new Date())) {
                return false;
            }
            UUID studentID = UUID.fromString(decodedJWT.getClaim("id").asString());
            return validateStudentID(studentID);
        } catch (JWTVerificationException e) {
            throw new PermissionDenialException(e, "invalidate access token");
        }
    }

    public boolean validateRefreshToken(String accessToken, String refreshToken) {
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        if (decodedJWT.getExpiresAt().before(new Date())
                || !decodedJWT.getClaim("accessToken").asString().equals(accessToken)) {
            return false;
        }
        UUID studentID = UUID.fromString(decodedJWT.getClaim("accessToken").asString());
        return validateStudentID(studentID);
    }

    private boolean validateStudentID(UUID studentID) {
        StudentDto studentDto = studentService.getStudentByID(studentID);
        return studentDto.id().equals(studentID);
    }


    public UUID getStudentIdByAccessToken(String accessToken) {
        DecodedJWT decodedJWT = verifier.verify(accessToken);
        return UUID.fromString(decodedJWT.getClaim("id").asString());
    }
}
