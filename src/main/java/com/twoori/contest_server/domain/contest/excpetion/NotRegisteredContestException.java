package com.twoori.contest_server.domain.contest.excpetion;

import com.twoori.contest_server.global.exception.BadRequestException;

import java.util.UUID;

public class NotRegisteredContestException extends BadRequestException {
    private final UUID studentId;
    private final UUID contestID;

    public NotRegisteredContestException(UUID studentId, UUID contestID) {
        super("not registered contest");
        this.studentId = studentId;
        this.contestID = contestID;
    }

    @Override
    public String getParamsInfo() {
        return "studentId: " + studentId + ", contestID: " + contestID;
    }
}
