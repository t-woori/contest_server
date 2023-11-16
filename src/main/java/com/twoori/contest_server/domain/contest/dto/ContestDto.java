package com.twoori.contest_server.domain.contest.dto;

import com.twoori.contest_server.domain.contest.dao.Contest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link Contest}
 */
public record ContestDto(UUID id, String authCode, String name, String hostName, LocalDateTime runningStartDateTime,
                         LocalDateTime runningEndDateTime, Double rateAccuracyByScoring,
                         Double rateTimeByScoring) implements Serializable {
}