package com.twoori.contest_server.domain.contest.vo;

import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

@Getter
public class EndContestVO extends APIOkMessageVO {
    private final Long diffTime;

    public EndContestVO(Long diffTime) {
        super();
        this.diffTime = diffTime;
    }
}
