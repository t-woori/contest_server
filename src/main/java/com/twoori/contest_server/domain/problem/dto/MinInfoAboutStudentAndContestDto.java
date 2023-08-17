package com.twoori.contest_server.domain.problem.dto;

import java.util.UUID;

public record MinInfoAboutStudentAndContestDto(
        UUID contestId,
        UUID studentId
) {
}
