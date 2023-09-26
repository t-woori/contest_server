package com.twoori.contest_server.domain.problem.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemVO(
        Long problemId,
        PROBLEM_TYPE problemType,

        CHAPTER_TYPE chapterType,
        GRADE problemGrade,
        String imageURL,
        List<ContentVO> contents) {
}
