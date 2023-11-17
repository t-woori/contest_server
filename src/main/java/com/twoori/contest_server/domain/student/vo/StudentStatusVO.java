package com.twoori.contest_server.domain.student.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twoori.contest_server.domain.contest.vo.ContestStatusVo;

public record StudentStatusVO(@JsonProperty("running_contest") ContestStatusVo contestStatusVO) {
}
