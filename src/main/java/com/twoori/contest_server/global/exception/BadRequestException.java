package com.twoori.contest_server.global.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends APIException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST, message);
    }
}
