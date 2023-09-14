package com.twoori.contest_server.domain.contest.excpetion;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.global.exception.PermissionDenialException;

import java.util.UUID;

public class EarlyEnterTimeException extends PermissionDenialException {
    private final UUID studentId;
    private final EnterContestDto contest;

    public EarlyEnterTimeException(UUID studentId, EnterContestDto contest) {
        super("early contest");
        this.studentId = studentId;
        this.contest = contest;
    }

    @Override
    public String getParamsInfo() {
        return "studentId[" + studentId + "] ," + contest;
    }
}
