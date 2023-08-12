package com.twoori.contest_server.global.vo;

import org.springframework.http.HttpStatus;

public class APIOkMessageVO extends CommonAPIResponseVO {
    public APIOkMessageVO() {
        super(HttpStatus.OK.value(), "ok");
    }
}
