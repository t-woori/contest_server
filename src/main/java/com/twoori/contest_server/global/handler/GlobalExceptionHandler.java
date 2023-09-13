package com.twoori.contest_server.global.handler;

import com.twoori.contest_server.global.exception.APIException;
import com.twoori.contest_server.global.vo.CommonAPIResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonAPIResponseVO> handleException(Exception e) {
        log.error("!!!Not Handling Exception!!!", e);
        return ResponseEntity.internalServerError()
                .body(new CommonAPIResponseVO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal error"));
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<CommonAPIResponseVO> handleAPIException(APIException e) {
        log.error("API Exception {}\nParameters: {}", e, e.getParamsInfo());
        return ResponseEntity.status(e.getHttpStatus())
                .body(new CommonAPIResponseVO(e.getHttpStatus().value(), e.getMessage()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<CommonAPIResponseVO> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.error("Missing Request Header Exception", e);
        return ResponseEntity.badRequest()
                .body(new CommonAPIResponseVO(HttpStatus.BAD_REQUEST.value(), "empty " + e.getHeaderName() + " header"));
    }
}
