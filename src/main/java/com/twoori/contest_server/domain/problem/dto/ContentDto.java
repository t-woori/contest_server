package com.twoori.contest_server.domain.problem.dto;

import java.io.Serializable;

public record ContentDto(Long ProblemID, Long ContentID, String preScript, String question,
                         String answer, String postScript) implements Serializable {
}