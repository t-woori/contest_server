package com.twoori.contest_server.domain.problem.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CHAPTER_TYPE {
    CAFFEE(0),
    DRUG(1),
    BEAUTY_SALON(2),
    PC_ROOM(3),
    RESTAURANT(4);
    @JsonValue
    private final int value;

    CHAPTER_TYPE(int value) {
        this.value = value;
    }

}
