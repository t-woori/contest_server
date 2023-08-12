package com.twoori.contest_server.global.exception;

import org.springframework.http.HttpStatus;

public class OKException extends APIException {
    public OKException(String message) {
        super(HttpStatus.OK, message);
    }

    public OKException(Throwable cause, String message) {
        super(cause, HttpStatus.NO_CONTENT, message);
    }
}
