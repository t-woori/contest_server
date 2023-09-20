package com.twoori.contest_server.domain.contest.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContestDto(
        UUID id,
        String name,
        String hostName,
        LocalDateTime runningStartDateTime,
        LocalDateTime runningEndDateTime
) implements Serializable {

}