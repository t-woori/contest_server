package com.twoori.contest_server.domain.problem.dto;

import com.twoori.contest_server.domain.problem.dao.ProblemInContest;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link ProblemInContest}
 */
public record ProblemInContestDto(Long problemID, UUID contestID,
                                  Long noOfProblemInContest) implements Serializable {
}