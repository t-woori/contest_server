package com.twoori.contest_server.domain.problem.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponseTotalStatusVO extends APIOkMessageVO {
        @JsonProperty("status_of_problem")
        private final List<Long> countOfProblemInStudent;

    public ResponseTotalStatusVO(List<Long> countOfProblemInStudent) {
        this.countOfProblemInStudent = countOfProblemInStudent;
    }
}
