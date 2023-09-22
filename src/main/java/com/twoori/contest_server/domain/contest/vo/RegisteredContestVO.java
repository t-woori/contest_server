package com.twoori.contest_server.domain.contest.vo;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisteredContestVO(
        UUID id,
        String name,
        LocalDateTime startedAt,
        LocalDateTime endedAt) {
}
