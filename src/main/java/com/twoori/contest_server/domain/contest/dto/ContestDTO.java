package com.twoori.contest_server.domain.contest.dto;

import com.twoori.contest_server.domain.contest.dao.Contest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContestDTO(
        UUID id,
        String authCode,
        String name,
        String hostName,
        LocalDateTime runningStartDateTime,
        LocalDateTime runningEndDateTime
) implements Serializable {

    public static ContestDTO daoToDto(Contest dao) {
        return new ContestDTO(
                dao.getId(),
                dao.getAuthCode(),
                dao.getName(),
                dao.getHostName(),
                dao.getRunningStartDateTime(),
                dao.getRunningEndDateTime()
        );
    }
}