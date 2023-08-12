package com.twoori.contest_server.domain.contest.vo;

import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EnterContestVOAPI extends APIOkMessageVO {

    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    public EnterContestVOAPI(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
