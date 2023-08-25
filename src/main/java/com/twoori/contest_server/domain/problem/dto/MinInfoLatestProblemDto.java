package com.twoori.contest_server.domain.problem.dto;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;


@Getter
public class MinInfoLatestProblemDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long problemId;
    private final Long contentId;
    private final Long noOfProblemInContest;

    public MinInfoLatestProblemDto(Long problemId,
                                   Long contentId,
                                   Long noOfProblemInContest) {
        this.problemId = problemId;
        this.contentId = contentId;
        this.noOfProblemInContest = noOfProblemInContest;
    }

    public static MinInfoLatestProblemDto of(Long problemId,
                                             Long contentId,
                                             Long noOfProblemInContest) {
        return new MinInfoLatestProblemDto(problemId, contentId, noOfProblemInContest);
    }
}
