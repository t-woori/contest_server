package com.twoori.contest_server.domain.problem.dto;

public record MinInfoAboutStudentAndContestWithScoreCondition(
        MinInfoAboutStudentAndContestDto minInfoAboutStudentAndContestDto,
        Double score
) {
}
