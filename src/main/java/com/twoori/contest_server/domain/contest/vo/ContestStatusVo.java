package com.twoori.contest_server.domain.contest.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twoori.contest_server.domain.problem.vo.ProblemStatusVo;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContestStatusVo(UUID contestID, LocalDateTime startedAt, LocalDateTime endedAt,
                              @JsonProperty("status") ProblemStatusVo problemStatusVo) {
}
