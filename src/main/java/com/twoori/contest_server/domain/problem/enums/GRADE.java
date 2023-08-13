package com.twoori.contest_server.domain.problem.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum GRADE {
    ELEMENTARY(0),
    MIDDLE(1),
    HIGH(2);
    @JsonValue
    private final int value;

    GRADE(int value) {
        this.value = value;
    }

}
