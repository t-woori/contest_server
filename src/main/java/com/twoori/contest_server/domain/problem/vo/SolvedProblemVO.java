package com.twoori.contest_server.domain.problem.vo;

import java.util.UUID;

public record SolvedProblemVO(
        UUID studentId, UUID contestId, Long noOfProblemInContest, Long contentId, Double newScore) {
}
