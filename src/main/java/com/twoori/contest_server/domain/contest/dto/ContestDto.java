package com.twoori.contest_server.domain.contest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContestDto(UUID contestId, LocalDateTime startTime, LocalDateTime localDateTime) {
}
