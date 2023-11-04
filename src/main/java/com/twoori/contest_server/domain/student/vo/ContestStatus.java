package com.twoori.contest_server.domain.student.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContestStatus(UUID contestID, LocalDateTime startedAt, LocalDateTime endedAt,
                            @JsonProperty("status") ProblemStatus problemStatus) {
}
