package com.twoori.contest_server.domain.contest.excpetion;

import com.twoori.contest_server.global.exception.PermissionDenialException;

import java.util.UUID;

public class NotCancelRegisterContest extends PermissionDenialException {

    private final UUID studentId;
    private final UUID contestId;

    public NotCancelRegisterContest(UUID studentId, UUID contestId) {
        super("not cancel time");
        this.studentId = studentId;
        this.contestId = contestId;
    }

    @Override
    public String getParamsInfo() {
        return "studentId=" + studentId + ", contestId=" + contestId;
    }
}
