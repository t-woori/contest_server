package com.twoori.contest_server.global.handler;

import com.twoori.contest_server.global.exception.APIException;
import com.twoori.contest_server.global.vo.ErrorMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageVO> handleException(Exception e) {
        log.error("!!!Not Handling Exception!!!", e);
        return ResponseEntity.internalServerError()
                .body(new ErrorMessageVO("internal error"));
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorMessageVO> handleAPIException(APIException e) {
        log.error("API Exception", e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorMessageVO(e.getMessage()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorMessageVO> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.error("Missing Request Header Exception", e);
        return ResponseEntity.badRequest()
                .body(new ErrorMessageVO("empty " + e.getHeaderName() + " header"));
    }
}
