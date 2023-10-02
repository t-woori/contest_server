package com.twoori.contest_server.domain.contest.repository;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ContestCondition {

    private LocalDateTime from;
    private LocalDateTime to;
    private String parameter;
    private UUID registeredStudentId;

}
