package com.twoori.contest_server.domain.contest.vo;

import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

import java.util.List;

@Getter
public class ContestsVo extends APIOkMessageVO {
    private final List<ContestVO> contests;

    public ContestsVo(List<ContestVO> contests) {
        this.contests = contests;
    }
}
