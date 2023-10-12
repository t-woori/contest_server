package com.twoori.contest_server.domain.student.dto;

import java.util.UUID;

public record StudentInContestIdDto(UUID contestId, UUID studentID) {

    public String getRedisKey() {
        return contestId.toString() + "_" + studentID.toString();
    }
}
