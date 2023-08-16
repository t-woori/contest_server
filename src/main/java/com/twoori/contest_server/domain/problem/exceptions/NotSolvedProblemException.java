package com.twoori.contest_server.domain.problem.exceptions;

import lombok.Getter;

@Getter

public class NotSolvedProblemException extends RuntimeException {
    private final long noSolvedProblemNo;

    public NotSolvedProblemException(long noSolvedProblemNo) {
        super();
        this.noSolvedProblemNo = noSolvedProblemNo;
    }
}
