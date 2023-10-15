package com.twoori.contest_server.domain.student.dao;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link StudentInContest}
 */
public record StudentInContestDto(UUID idStudentID, UUID idContestID) implements Serializable {
}