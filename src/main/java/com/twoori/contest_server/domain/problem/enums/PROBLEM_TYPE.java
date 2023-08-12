package com.twoori.contest_server.domain.problem.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PROBLEM_TYPE {
    BLANK(0),
    MEMORIZE(1);
    @JsonValue
    private final int value;

    PROBLEM_TYPE(int value) {
        this.value = value;
    }

}
