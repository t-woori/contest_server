package com.twoori.contest_server.global.security;

import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.util.Utils;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    private final Utils utils;
    private final StudentJwtProvider studentJwtProvider;

    public SecurityUtil(Utils utils, StudentJwtProvider studentJwtProvider) {
        this.utils = utils;
        this.studentJwtProvider = studentJwtProvider;
    }

    public StudentDto validateAuthorization(String accessTokenHeader) {
        String accessToken = utils.parseAccessTokenAboutAuthorizationHeader(accessTokenHeader);
        return studentJwtProvider.validateAccessToken(accessToken);
    }

}
