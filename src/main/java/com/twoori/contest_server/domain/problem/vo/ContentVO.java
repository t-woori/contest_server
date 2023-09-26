package com.twoori.contest_server.domain.problem.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContentVO(
        Long contentId,
        String answer,
        String preScript,
        String question,
        String postScript,
        String hint
) {
}
