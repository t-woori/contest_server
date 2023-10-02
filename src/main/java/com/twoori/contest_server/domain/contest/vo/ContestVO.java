package com.twoori.contest_server.domain.contest.vo;

import java.time.LocalDateTime;
import java.util.UUID;


public record ContestVO(UUID contestId, String name, String hostName, LocalDateTime startedAt, LocalDateTime endedAt) {

}