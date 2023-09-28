package com.twoori.contest_server.domain.problem.repository;

import lombok.Data;

import java.util.UUID;

@Data
public class ProblemCondition {
    private UUID contestId;
    private Long noOfProblemInContest;
}
