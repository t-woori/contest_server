package com.twoori.contest_server.domain.problem.dto;

import java.util.UUID;

public record LogStudentInProblemIdDto(
        UUID contestId,
        UUID studentId,
        long problemId,
        long contentId
) {

}
