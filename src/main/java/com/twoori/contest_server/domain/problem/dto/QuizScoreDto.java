package com.twoori.contest_server.domain.problem.dto;

import java.util.UUID;

public record QuizScoreDto(
        UUID contestId,
        UUID studentId,
        long problemId,
        long contentId,
        double score
) {
}
