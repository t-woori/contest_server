package com.twoori.contest_server.domain.contest.vo;

import java.time.LocalDateTime;
import java.util.UUID;

public record SearchContestVO(
        UUID contestId,
        String name,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Boolean isRegistered
) {
}
