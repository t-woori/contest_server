package com.twoori.contest_server.domain.contest.service;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnterContestDtoForController(UUID id, LocalDateTime runningStartDateTime,
                                           LocalDateTime runningEndDateTime) {


}
