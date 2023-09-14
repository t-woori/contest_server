package com.twoori.contest_server.domain.contest.excpetion;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.global.exception.PermissionDenialException;

import java.util.UUID;

public class EndContestException extends PermissionDenialException {
    private final UUID studentId;
    private final EnterContestDto contest;

    public EndContestException(UUID studentId, EnterContestDto contest) {
        super("end contest");
        this.studentId = studentId;
        this.contest = contest;
    }

    @Override
    public String getParamsInfo() {
        return "studentId[" + studentId + "] ," + contest;
    }
}
