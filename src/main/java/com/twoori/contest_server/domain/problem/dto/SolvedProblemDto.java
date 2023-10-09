package com.twoori.contest_server.domain.problem.dto;

import java.util.UUID;

public record SolvedProblemDto(
        UUID contestId, UUID studentId, Long noOfProblemInContest, Long contentId, Double newScore) {
}
