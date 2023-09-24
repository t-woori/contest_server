package com.twoori.contest_server.domain.problem.exceptions;

import com.twoori.contest_server.global.exception.NotFoundException;

import java.util.UUID;

public class NotFoundProblemException extends NotFoundException {
    private final UUID contestId;
    private final Long problemId;

    public NotFoundProblemException(UUID contestId, Long problemId) {
        super("not found problem");
        this.contestId = contestId;
        this.problemId = problemId;
    }

    @Override
    public String getParamsInfo() {
        return "contestId: " + contestId + " problemId: " + problemId;
    }
}
