package com.twoori.contest_server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class APIException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public APIException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }


    public String getParamsInfo() {
        return "APIException{" +
                "httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                '}';
    }

}
