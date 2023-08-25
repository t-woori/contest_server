package com.twoori.contest_server.domain.problem.dto;

import java.io.Serializable;
import java.util.UUID;

public record ProblemInContestDto(Long problemId, UUID contestId,
                                  Long noOfProblemInContest) implements Serializable {
}