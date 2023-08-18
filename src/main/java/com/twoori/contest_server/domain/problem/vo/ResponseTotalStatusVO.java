package com.twoori.contest_server.domain.problem.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseTotalStatusVO(
        @JsonProperty("status_of_problem")
        List<Long> countOfProblemInStudent,
        Integer status, String message
) {
}
