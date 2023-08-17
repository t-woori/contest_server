package com.twoori.contest_server.domain.problem.vo;

public record RequestUpdateStatusVO(
        long problemId,
        long contentId,
        double score

) {
}
