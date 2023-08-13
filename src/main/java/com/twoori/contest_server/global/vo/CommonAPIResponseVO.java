package com.twoori.contest_server.global.vo;

import lombok.Getter;

@Getter
public class CommonAPIResponseVO {

    protected final Integer status;
    protected final String message;

    public CommonAPIResponseVO(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
