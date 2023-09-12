package com.twoori.contest_server.domain.contest.service;

import java.time.LocalDateTime;

public record EnterContestDtoForController(LocalDateTime startDateTime, LocalDateTime endDateTime) {

}
