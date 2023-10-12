package com.twoori.contest_server.domain.contest.vo;

import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

@Getter
public class EndContestVO extends APIOkMessageVO {
    private final Double average;
    private final Long diffTime;

    public EndContestVO(Double average, Long diffTime) {
        this.average = average;
        this.diffTime = diffTime;
    }
}
