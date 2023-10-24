package com.twoori.contest_server.domain.student.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StudentStatusVO(@JsonProperty("running_contest") ContestStatus contestStatus) {
}
