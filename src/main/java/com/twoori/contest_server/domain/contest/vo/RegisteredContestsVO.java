package com.twoori.contest_server.domain.contest.vo;

import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

import java.util.List;

@Getter
public class RegisteredContestsVO extends APIOkMessageVO {
    private final List<RegisteredContestVO> contests;

    public RegisteredContestsVO(List<RegisteredContestVO> contests) {
        this.contests = contests;
    }
}
