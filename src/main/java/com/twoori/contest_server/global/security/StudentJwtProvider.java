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
    private final String CLAIM_STUDENT_ID = "student_id";
    private final String CLAIM_ACCESS_TOKEN = "access_token";
    private final String MESSAGE_INVALIDATE_ACCESS_TOKEN = "invalidate access token";
    private final Algorithm jwtAlgorithm;
    private final JWTVerifier verifier;

    public StudentJwtProvider(StudentService studentService, @Value("${jwt.secret}") String jwtSecretKey) {
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
                .withClaim(CLAIM_STUDENT_ID, studentDto.id().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .sign(jwtAlgorithm);
    }

    private String createRefreshToken(StudentDto studentDto, String accessToken) {
        return JWT.create()
                .withClaim(CLAIM_STUDENT_ID, studentDto.id().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                .withClaim(CLAIM_ACCESS_TOKEN, accessToken)
                .sign(jwtAlgorithm);
    }

    public StudentDto validateAccessToken(String accessToken) {
        try {
            DecodedJWT decodedJWT = verifier.verify(accessToken);
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new PermissionDenialException(MESSAGE_INVALIDATE_ACCESS_TOKEN);
            }
            return getStudentByDecodedJWT(decodedJWT);
        } catch (JWTVerificationException e) {
            throw new PermissionDenialException(e, MESSAGE_INVALIDATE_ACCESS_TOKEN);
        }
    }

    public StudentDto validateRefreshToken(String accessToken, String refreshToken) {
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        if (decodedJWT.getExpiresAt().before(new Date())
                || !decodedJWT.getClaim(CLAIM_ACCESS_TOKEN).asString().equals(accessToken)) {
            throw new PermissionDenialException(MESSAGE_INVALIDATE_ACCESS_TOKEN);
        }
        UUID studentID = UUID.fromString(decodedJWT.getClaim(CLAIM_STUDENT_ID).asString());
        return studentService.getStudentByID(studentID);
    }

    public StudentDto getStudentByDecodedJWT(DecodedJWT decodedJWT) {
        if (!decodedJWT.getClaims().containsKey(CLAIM_STUDENT_ID)) {
            throw new PermissionDenialException(MESSAGE_INVALIDATE_ACCESS_TOKEN);
        }
        UUID studentID = UUID.fromString(decodedJWT.getClaim(CLAIM_STUDENT_ID).asString());
        return studentService.getStudentByID(studentID);
    }
}
