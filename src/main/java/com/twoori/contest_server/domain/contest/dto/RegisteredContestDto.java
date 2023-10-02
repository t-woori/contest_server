package com.twoori.contest_server.domain.contest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisteredContestDto(
        UUID contestId,
        String name,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {
}
