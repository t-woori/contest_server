package com.twoori.contest_server.domain.contest.excpetion;

import com.twoori.contest_server.global.exception.PermissionDenialException;

public class ForbiddenRegisterContestException extends PermissionDenialException {
    public ForbiddenRegisterContestException(String message) {
        super(message);
    }
}
