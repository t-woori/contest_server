package com.twoori.contest_server.domain.contest.excpetion;

import com.twoori.contest_server.global.exception.NotFoundException;

import java.util.UUID;

public class NotFoundRegisteredContestException extends NotFoundException {

    private final UUID studentId;
    private final UUID contestId;

    public NotFoundRegisteredContestException(UUID studentId, UUID contestId) {
        super("not found contest");
        this.studentId = studentId;
        this.contestId = contestId;
    }

    @Override
    public String getParamsInfo() {
        return "studentId[" + studentId + "], contestId[" + contestId + "]";
    }
}
