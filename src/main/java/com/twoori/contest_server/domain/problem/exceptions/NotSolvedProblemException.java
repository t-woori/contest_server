package com.twoori.contest_server.domain.problem.exceptions;

import com.twoori.contest_server.domain.problem.dto.MinInfoLatestProblemDto;
import lombok.Getter;

@Getter

public class NotSolvedProblemException extends RuntimeException {
    private final MinInfoLatestProblemDto latestProblemInfoDto;

    public NotSolvedProblemException(MinInfoLatestProblemDto latestProblemInfoDto) {
        this.latestProblemInfoDto = latestProblemInfoDto;
    }
}
