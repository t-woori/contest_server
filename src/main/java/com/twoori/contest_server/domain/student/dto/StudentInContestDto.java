package com.twoori.contest_server.domain.student.dto;

import java.time.LocalDateTime;


public record StudentInContestDto(LocalDateTime startedAt, LocalDateTime endedAt, Boolean isEntered,
                                  Boolean isResigned) {

}
