package com.twoori.contest_server.domain.contest.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchContestsVO extends APIOkMessageVO {

    @JsonProperty("contests")
    private final List<SearchContestVO> searchContestVOList;

    public SearchContestsVO(List<SearchContestVO> searchContestVOList) {
        this.searchContestVOList = searchContestVOList;
    }
}
