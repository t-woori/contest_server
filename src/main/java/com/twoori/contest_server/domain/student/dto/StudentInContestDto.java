package com.twoori.contest_server.domain.student.dto;

import java.time.LocalDateTime;


public record StudentInContestDto(LocalDateTime startedAt, LocalDateTime endContestDateTime,
                                  LocalDateTime endContestAt) {

}
