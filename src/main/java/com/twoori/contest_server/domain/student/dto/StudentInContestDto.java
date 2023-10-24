package com.twoori.contest_server.domain.student.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public record StudentInContestDto(UUID studentId, UUID contestId, LocalDateTime startedAt,
                                  LocalDateTime endContestDateTime,
                                  LocalDateTime endContestAt) {

}
