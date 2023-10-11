package com.twoori.contest_server.domain.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StudentInContestDto {

    private final LocalDateTime startedAt;

    private final LocalDateTime endedAt;

    private final Boolean isEntered;

    private final Boolean isResigned;


}
