package com.twoori.contest_server.domain.problem.dao.enums;

import lombok.Getter;

@Getter
public enum GRADE {
    ELEMENTARY(0),
    MIDDLE(1),
    HIGH(2);

    private final int value;

    GRADE(int value) {
        this.value = value;
    }

}
