package com.twoori.contest_server.domain.contest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SearchContestDtoForController(
        UUID id,
        String name,
        String hostName,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {
}
