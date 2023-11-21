package com.twoori.contest_server.global.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends APIException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }


}
