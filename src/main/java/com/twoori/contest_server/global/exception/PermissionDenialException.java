package com.twoori.contest_server.global.exception;

import org.springframework.http.HttpStatus;

public class PermissionDenialException extends APIException {
    public PermissionDenialException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

}
