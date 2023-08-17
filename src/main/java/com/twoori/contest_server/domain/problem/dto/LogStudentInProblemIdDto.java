package com.twoori.contest_server.domain.problem.dto;

import java.util.UUID;

public record LogStudentInProblemIdDto(
        UUID contestId,
        UUID studentId,
        Long problemId,
        Long contentId
) {

}
