package com.twoori.contest_server.global.vo;

import lombok.Getter;

public enum CommonMessage {
    OK("ok"),
    ;

    @Getter
    private final String message;

    CommonMessage(String message) {
        this.message = message;
    }
}
