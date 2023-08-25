package com.twoori.contest_server.domain.problem.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class InfoLatestProblemDto extends MinInfoLatestProblemDto implements Serializable {

    private final Double score;

    public InfoLatestProblemDto(Long problemId,
                                Long contentId,
                                Long noOfProblemInContest,
                                Double score) {
        super(problemId, contentId, noOfProblemInContest);
        this.score = score;
    }
}