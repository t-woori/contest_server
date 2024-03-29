package com.twoori.contest_server.domain.contest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CancelContestDto(UUID contestId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
}
