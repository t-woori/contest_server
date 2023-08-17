package com.twoori.contest_server.domain.problem.dto;

import java.io.Serializable;

public record ContentDtoForController(
        ProblemDtoForController problemDtoForController,
        Long contentID,
        String preScript,
        String question,
        String answer,
        String postScript
) implements Serializable {
}