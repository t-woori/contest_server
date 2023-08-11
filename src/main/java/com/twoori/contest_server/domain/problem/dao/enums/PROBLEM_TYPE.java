package com.twoori.contest_server.domain.problem.dao.enums;

import lombok.Getter;

@Getter
public enum PROBLEM_TYPE {
    BLANK(0),
    MEMORIZE(1);
    private final int value;

    PROBLEM_TYPE(int value) {
        this.value = value;
    }

}
