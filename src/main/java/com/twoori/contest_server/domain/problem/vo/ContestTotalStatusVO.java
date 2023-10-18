package com.twoori.contest_server.domain.problem.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

import java.util.List;

@Getter
public class ContestTotalStatusVO extends APIOkMessageVO {
    @JsonProperty("counts")
    private final List<Long> counts;

    public ContestTotalStatusVO(List<Long> counts) {
        this.counts = counts;
    }
}
