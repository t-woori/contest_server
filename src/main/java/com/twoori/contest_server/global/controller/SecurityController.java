package com.twoori.contest_server.global.controller;

import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.util.Utils;

public class SecurityController {
    private final Utils utils;
    private final StudentJwtProvider studentJwtProvider;

    public SecurityController(Utils utils, StudentJwtProvider studentJwtProvider) {
        this.utils = utils;
        this.studentJwtProvider = studentJwtProvider;
    }

    protected StudentDto validateAuthorization(String accessTokenHeader) {
        String accessToken = utils.parseAccessTokenAboutAuthorizationHeader(accessTokenHeader);
        return studentJwtProvider.validateAccessToken(accessToken);
    }
}
