package com.twoori.contest_server.domain.contest.vo;

import java.time.LocalDateTime;

public record EnterContestVO(
        String message,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
