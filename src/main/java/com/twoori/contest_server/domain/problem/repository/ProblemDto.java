package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;

import java.util.List;

public record ProblemDto(
        Long problemId,
        PROBLEM_TYPE problemType,
        CHAPTER_TYPE chapterType,
        GRADE problemGrade,

        String imageURL,
        List<ContentDto> contents) {
}
